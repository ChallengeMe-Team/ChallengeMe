import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FriendDTO, UserService } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import {ToastComponent} from '../../../shared/toast/toast-component';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-friends-list',
  standalone: true,
  imports: [CommonModule, ToastComponent, FormsModule],
  templateUrl: './friends-list.component.html',
  styleUrl: './friends-list.component.css'
})
export class FriendsListComponent implements OnInit {

  private userService = inject(UserService);
  private authService = inject(AuthService);

  currentUserId = '';
  friends = signal<FriendDTO[]>([]);
  isLoading = signal(false);

  // For the "Add Friend" input
  friendUsername = signal('');

  // Toast state
  toastMessage = signal('');
  toastType = signal<'success'|'error'>('success');
  showToast = signal(false);

  ngOnInit() {
    this.currentUserId = this.authService.currentUser()?.id;
    this.loadFriends();
  }

  loadFriends() {
    this.isLoading.set(true);
    this.userService.getFriends(this.currentUserId).subscribe({
      next: (data) => {
        this.friends.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.show('Error fetching friends', 'error');
        this.isLoading.set(false);
      }
    });
  }

  // ADD FRIEND FLOW
  addFriend() {
    const username = this.friendUsername().trim();

    if (!username) return;

    // Check if already friend
    if (this.friends().some(f => f.username === username)) {
      return this.show("User is already your friend", 'error');
    }

    // Search in DB
    this.userService.searchUser(username).subscribe({
      next: (user) => {
        // Prevent adding yourself
        if (user.username === this.authService.currentUser()?.username) {
          return this.show("You cannot add yourself", 'error');
        }

        this.userService.addFriend(this.currentUserId, username).subscribe({
          next: () => {
            this.show("Friend added successfully!", 'success');
            this.friendUsername.set('');
            this.loadFriends();
          },
          error: (err) => {
            this.show(err.error || "Failed to add friend", 'error');
          }
        });
      },
      error: () => this.show("User not found", 'error')
    });
  }

  // Toast helper
  private show(msg: string, type: 'success' | 'error') {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    this.showToast.set(true);
    setTimeout(() => this.showToast.set(false), 3000);
  }
}
