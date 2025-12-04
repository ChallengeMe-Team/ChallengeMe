import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { UserService } from '../../../services/user.service';
import { ToastComponent } from '../../../shared/toast/toast-component';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, ToastComponent],
  templateUrl: './settings-component.html',
  styleUrls: ['./settings-component.css']
})
export class SettingsComponent implements OnInit {
  private authService = inject(AuthService);
  private userService = inject(UserService);

  // Lista fisierelor (trebuie sa existe in src/assets/avatars/)
  availableAvatars = [
    'cat.png', 'dog.png', 'gamer.png',
    'monster.png', 'ninja.png', 'robot.png'
  ];

  selectedAvatar = signal<string>('avatar-1.png');
  currentUser: any = null;
  isLoading = signal(false);

  // Toast State
  showToast = signal(false);
  toastMessage = signal('');
  toastType = signal<'success' | 'error'>('success');

  ngOnInit() {
    this.currentUser = this.authService.currentUser();
    if (this.currentUser?.avatar) {
      this.selectedAvatar.set(this.currentUser.avatar);
    }
  }

  selectAvatar(avatar: string) {
    this.selectedAvatar.set(avatar);
  }

  saveChanges() {
    if (!this.currentUser) return;
    this.isLoading.set(true);

    const payload = {
      avatar: this.selectedAvatar()
    };

    this.userService.updateUser(this.currentUser.id, payload).subscribe({
      next: (updatedUser) => {
        // 1. Show Success
        this.triggerToast('Profile updated successfully!', 'success');

        // 2. Update Auth State (ca sa se vada in Navbar instant)
        // Combinam datele vechi (token) cu userul nou
        this.authService.currentUser.set({
          ...this.currentUser,
          avatar: updatedUser.avatar
        });

        this.isLoading.set(false);
      },
      error: () => {
        this.triggerToast('Failed to update profile.', 'error');
        this.isLoading.set(false);
      }
    });
  }

  private triggerToast(msg: string, type: 'success' | 'error') {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    this.showToast.set(true);
    setTimeout(() => this.showToast.set(false), 3000);
  }
}
