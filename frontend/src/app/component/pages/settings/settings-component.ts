import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { UserService } from '../../../services/user.service';
import { ToastComponent } from '../../../shared/toast/toast-component';
import { LucideAngularModule, User, Mail, Lock } from 'lucide-angular';

// Importuri RxJS (Esențiale!)
import { Subject, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';

// Definim tipurile posibile pentru modal
type ModalType = 'password' | 'username' | 'email' | null;

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, ToastComponent, FormsModule, LucideAngularModule],
  templateUrl: './settings-component.html',
  styleUrls: ['./settings-component.css']
})
export class SettingsComponent implements OnInit {
  private authService = inject(AuthService);
  private userService = inject(UserService);

  // Icons
  readonly icons = { User, Mail, Lock };

  // --- STATE ---
  currentUser: any = null;
  isLoading = signal(false);

  // --- AVATAR STATE ---
  availableAvatars = ['cat.png', 'dog.png', 'gamer.png', 'monster.png', 'ninja.png', 'robot.png'];
  selectedAvatar = signal<string>('avatar-1.png');

  // --- AVAILABILITY STATE ---
  // null = nu am verificat inca, true = e luat (eroare), false = e liber (succes)
  usernameTaken = signal<boolean | null>(null);
  emailTaken = signal<boolean | null>(null);
  isChecking = signal(false);

  // Subjects pentru Debounce (asteapta sa te opresti din scris)
  private usernameCheck$ = new Subject<string>();
  private emailCheck$ = new Subject<string>();

  // --- 🔄 MODAL STATE (Generalizat) ---
  isModalOpen = signal(false);
  activeModalType = signal<ModalType>(null);
  modalStep = signal<'confirm' | 'form'>('confirm');

  // --- FORM DATA SIGNALS ---
  // Password
  currentPassword = signal('');
  newPassword = signal('');
  confirmPassword = signal('');

  // Profile
  newUsername = signal('');
  newEmail = signal('');

  // --- 💡 VALIDARI VIZUALE (COMPUTED) ---

  // 1. Validare Parola
  hasMinLength = computed(() => this.newPassword().length >= 6);
  hasUpperCase = computed(() => /[A-Z]/.test(this.newPassword()));
  hasLowerCase = computed(() => /[a-z]/.test(this.newPassword()));
  hasNumber = computed(() => /[0-9]/.test(this.newPassword()));
  hasSpecialChar = computed(() => /[!@#$%^&*(),.?":{}|<>_]/.test(this.newPassword()));
  passwordsMatch = computed(() => this.newPassword() && this.newPassword() === this.confirmPassword());

  isPasswordValid = computed(() =>
    this.currentPassword().length > 0 &&
    this.hasMinLength() &&
    this.hasSpecialChar() &&
    this.passwordsMatch()
  );

  // 2. Validare Username
  usernameHasLength = computed(() => this.newUsername().trim().length >= 3);
  usernameIsDifferent = computed(() => this.newUsername() !== this.currentUser?.username);

  // Helper pentru checklist (verde doar daca serverul a zis explicit FALSE)
  usernameIsUnique = computed(() => this.usernameTaken() === false);

  isUsernameValid = computed(() =>
    this.usernameHasLength() &&
    this.usernameIsDifferent() &&
    this.usernameTaken() !== true // Validam doar daca NU e luat (permitem null)
  );

  // 3. Validare Email
  emailHasFormat = computed(() => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.newEmail().trim()));
  emailIsDifferent = computed(() => this.newEmail().trim() !== this.currentUser?.email);

  // Helper pentru checklist
  emailIsUnique = computed(() => this.emailTaken() === false);

  isEmailValid = computed(() =>
    this.emailHasFormat() &&
    this.emailIsDifferent() &&
    this.emailTaken() !== true
  );

  // --- TOAST ---
  showToast = signal(false);
  toastMessage = signal('');
  toastType = signal<'success' | 'error'>('success');

  ngOnInit() {
    this.currentUser = this.authService.currentUser();
    if (this.currentUser?.avatar) {
      this.selectedAvatar.set(this.currentUser.avatar);
    }

    this.setupAvailabilityCheckers();
  }

  // --- ACTIONS: MODAL CONTROL ---

  // Configurare RxJS
  private setupAvailabilityCheckers() {
    // 1. Username Checker
    this.usernameCheck$.pipe(
      debounceTime(500), // Asteapta 500ms dupa ultima tasta
      distinctUntilChanged(),
      switchMap(username => {
        if (!username || username.length < 3 || username === this.currentUser?.username) {
          return of(null); // Nu verifica daca e invalid sau e al meu
        }
        this.isChecking.set(true);
        return this.userService.checkUsername(username).pipe(
          catchError(() => of(null)) // In caz de eroare, ignoram
        );
      })
    ).subscribe(isTaken => {
      this.isChecking.set(false);
      this.usernameTaken.set(isTaken);
    });

    // 2. Email Checker
    this.emailCheck$.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(email => {
        if (!email || !email.includes('@') || email === this.currentUser?.email) {
          return of(null);
        }
        this.isChecking.set(true);
        return this.userService.checkEmail(email).pipe(
          catchError(() => of(null))
        );
      })
    ).subscribe(isTaken => {
      this.isChecking.set(false);
      this.emailTaken.set(isTaken);
    });
  }

  // HANDLERS PT INPUT (Leaga HTML de RxJS)
  onUsernameInput(value: string) {
    this.newUsername.set(value);
    this.usernameTaken.set(null); // Resetam starea pana vine raspunsul
    this.usernameCheck$.next(value.trim()); // Declansam verificarea
  }

  onEmailInput(value: string) {
    this.newEmail.set(value);
    this.emailTaken.set(null);
    this.emailCheck$.next(value.trim());
  }

  openModal(type: ModalType) {
    this.activeModalType.set(type);
    this.modalStep.set('confirm');

    // Resetam input-urile cu valorile curente
    if (type === 'username') this.newUsername.set(this.currentUser.username);
    if (type === 'email') this.newEmail.set(this.currentUser.email);
    if (type === 'password') {
      this.currentPassword.set('');
      this.newPassword.set('');
      this.confirmPassword.set('');
    }

    this.isModalOpen.set(true);
  }

  proceedToForm() {
    this.modalStep.set('form');
  }

  closeModal() {
    this.isModalOpen.set(false);
    this.activeModalType.set(null);
  }

  // --- ACTIONS: SAVE DATA ---

  // 1. Avatar (Direct Save)
  saveAvatar() {
    this.performUpdate({ avatar: this.selectedAvatar() }, 'Avatar updated!');
  }

  // 2. Username Submit
  onUpdateUsername() {
    if (!this.isUsernameValid()) return;
    this.performUpdate({ username: this.newUsername().trim() }, 'Username updated successfully!');
  }

  // 3. Email Submit
  onUpdateEmail() {
    if (!this.isEmailValid()) return;
    this.performUpdate({ email: this.newEmail().trim() }, 'Email updated successfully!');
  }

  // 4. Password Submit
  onUpdatePassword() {
    if (!this.isPasswordValid()) return;

    this.isLoading.set(true);
    const payload = {
      currentPassword: this.currentPassword(),
      newPassword: this.newPassword()
    };

    this.userService.changePassword(this.currentUser.id, payload).subscribe({
      next: () => {
        this.triggerToast('Password updated successfully!', 'success');
        this.closeModal();
        this.isLoading.set(false);
      },
      error: (err) => {
        const msg = err.error?.error || 'Incorrect current password.';
        this.triggerToast(msg, 'error');
        this.isLoading.set(false);
      }
    });
  }

  // Helper generic
  private performUpdate(payload: any, successMsg: string) {
    if (!this.currentUser) return;
    this.isLoading.set(true);

    this.userService.updateUser(this.currentUser.id, payload).subscribe({
      next: (updatedUser) => {
        this.triggerToast(successMsg, 'success');

        const mergedUser = { ...this.currentUser, ...updatedUser };
        this.currentUser = mergedUser;
        this.authService.currentUser.set(mergedUser);

        this.closeModal();
        this.isLoading.set(false);
      },
      error: (err) => {
        // Afisam eroarea de unicitate din backend
        const msg = err.error?.error || 'Update failed.';
        this.triggerToast(msg, 'error');
        this.isLoading.set(false);
      }
    });
  }

  // Avatar select logic
  selectAvatar(avatar: string) {
    this.selectedAvatar.set(avatar);
  }

  private triggerToast(msg: string, type: 'success' | 'error') {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    this.showToast.set(true);
    setTimeout(() => this.showToast.set(false), 3000);
  }
}
