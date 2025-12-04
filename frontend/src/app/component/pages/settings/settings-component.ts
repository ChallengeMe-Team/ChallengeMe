import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { UserService } from '../../../services/user.service';
import { ToastComponent } from '../../../shared/toast/toast-component';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, ToastComponent, FormsModule],
  templateUrl: './settings-component.html',
  styleUrls: ['./settings-component.css']
})
export class SettingsComponent implements OnInit {
  private authService = inject(AuthService);
  private userService = inject(UserService);

  // --- AVATAR STATE ---
  availableAvatars = [
    'cat.png', 'dog.png', 'gamer.png',
    'monster.png', 'ninja.png', 'robot.png'
  ];
  selectedAvatar = signal<string>('avatar-1.png');
  currentUser: any = null;
  isLoading = signal(false);

  // --- PASSWORD MODAL STATE ---
  // Controleaza daca modalul e deschis
  isPasswordModalOpen = signal(false);
  // Controleaza pasul: 'confirm' (Are you sure?) sau 'form' (Inputurile)
  modalStep = signal<'confirm' | 'form'>('confirm');

  // --- PASSWORD FORM SIGNALS ---
  currentPassword = signal('');
  newPassword = signal('');
  confirmPassword = signal('');
  isLoadingPassword = signal(false);

  // --- TOAST STATE ---
  showToast = signal(false);
  toastMessage = signal('');
  toastType = signal<'success' | 'error'>('success');

  // --- VALIDARE PAROLA IN TIMP REAL (COMPUTED SIGNALS) ---
  hasMinLength = computed(() => this.newPassword().length >= 6);
  hasUpperCase = computed(() => /[A-Z]/.test(this.newPassword()));
  hasLowerCase = computed(() => /[a-z]/.test(this.newPassword()));
  hasNumber = computed(() => /[0-9]/.test(this.newPassword()));
  hasSpecialChar = computed(() => /[!@#$%^&*(),.?":{}|<>_-]/.test(this.newPassword()));
  passwordsMatch = computed(() => this.newPassword() && this.newPassword() === this.confirmPassword());

  // Formularul e valid DOAR daca toate conditiile sunt true
  isFormValid = computed(() =>
    this.currentPassword().length > 0 &&
    this.hasMinLength() &&
    this.hasUpperCase() &&
    this.hasLowerCase() &&
    this.hasNumber() &&
    this.hasSpecialChar() &&
    this.passwordsMatch()
  );

  ngOnInit() {
    this.currentUser = this.authService.currentUser();
    if (this.currentUser?.avatar) {
      this.selectedAvatar.set(this.currentUser.avatar);
    }
  }

  // --- ACTIONS: AVATAR ---
  selectAvatar(avatar: string) {
    this.selectedAvatar.set(avatar);
  }

  saveChanges() {
    if (!this.currentUser) return;
    this.isLoading.set(true);
    const payload = { avatar: this.selectedAvatar() };

    this.userService.updateUser(this.currentUser.id, payload).subscribe({
      next: (updatedUser) => {
        this.triggerToast('Profile updated successfully!', 'success');
        this.authService.currentUser.set({ ...this.currentUser, avatar: updatedUser.avatar });
        this.isLoading.set(false);
      },
      error: () => {
        this.triggerToast('Failed to update profile.', 'error');
        this.isLoading.set(false);
      }
    });
  }

  // --- ACTIONS: PASSWORD FLOW ---

  // Deschide Modalul la pasul de Confirmare
  openPasswordModal() {
    this.modalStep.set('confirm');
    this.resetPasswordForm();
    this.isPasswordModalOpen.set(true);
  }

  // Treci la Formular
  proceedToForm() {
    this.modalStep.set('form');
  }

  // Inchide tot
  closeModal() {
    this.isPasswordModalOpen.set(false);
    this.resetPasswordForm();
  }

  // Submit
  onChangePassword() {
    if (!this.currentUser || !this.isFormValid()) return;

    this.isLoadingPassword.set(true);

    const payload = {
      currentPassword: this.currentPassword(),
      newPassword: this.newPassword()
    };

    this.userService.changePassword(this.currentUser.id, payload).subscribe({
      next: () => {
        this.triggerToast('Password updated successfully!', 'success');
        this.closeModal(); // Inchidem modalul automat la succes
        this.isLoadingPassword.set(false);
      },
      error: (err) => {
        const msg = err.error?.error || 'Incorrect current password.';
        this.triggerToast(msg, 'error');
        this.isLoadingPassword.set(false);
      }
    });
  }

  private resetPasswordForm() {
    this.currentPassword.set('');
    this.newPassword.set('');
    this.confirmPassword.set('');
  }

  private triggerToast(msg: string, type: 'success' | 'error') {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    this.showToast.set(true);
    setTimeout(() => this.showToast.set(false), 3000);
  }
}
