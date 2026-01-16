import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FriendDTO, UserService } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import {ToastComponent} from '../../../shared/toast/toast-component';
import {FormsModule} from '@angular/forms';
import { computed } from '@angular/core';
import { forkJoin } from 'rxjs';
import { UserDTO } from '../../../services/user.service';
import { UserQuickViewComponent } from '../../../shared/user-quick-view/user-quick-view.component';
import { UserProfile } from '../../../models/user.model'; // Folosim modelul tău existent

/**
 * Component responsible for managing the user's social network.
 * Features a real-time global user search, friend request handling,
 * and detailed profile previews.
 * * Key Technical Aspects:
 * - Efficient Data Fetching: Uses 'forkJoin' to load both the friend list and
 * global user directory in a single parallel stream.
 * - Reactive Filtering: Implements a 'computed' signal for real-time search
 * that handles filtering, self-exclusion, and priority sorting.
 * - Interactive Search UX: Manages focus states with timed delays to allow
 * seamless interaction between the input and the result dropdown.
 */
@Component({
  selector: 'app-friends-list',
  standalone: true,
  imports: [CommonModule, ToastComponent, FormsModule, UserQuickViewComponent],
  templateUrl: './friends-list.component.html',
  styleUrl: './friends-list.component.css'
})
export class FriendsListComponent implements OnInit {

  private userService = inject(UserService);
  private authService = inject(AuthService);

  currentUserId = '';

  /** Signal-based state for friend list and global user directory. */
  friends = signal<FriendDTO[]>([]);
  isLoading = signal(false);
  allUsers = signal<UserDTO[]>([]);

  // For the "Add Friend" input
  friendUsername = signal('');

  // Toast state
  toastMessage = signal('');
  toastType = signal<'success'|'error'>('success');
  showToast = signal(false);

  isSearchFocused = signal(false);

  /** Modern Search Logic:
   * 1. Filters by username or email.
   * 2. Excludes the current authenticated user.
   * 3. Sorts results: Non-friends appear first to facilitate new connections,
   * followed by existing friends, sorted alphabetically.
   */
  filteredUsers = computed(() => {
    const search = this.friendUsername().toLowerCase().trim();
    const all = this.allUsers();
    const currentUser = this.authService.currentUser()?.username;

    let usersToShow = all.filter(u => u.username !== currentUser);

    if (search) {
      usersToShow = usersToShow.filter(u =>
        u.username.toLowerCase().includes(search) || u.email?.toLowerCase().includes(search)
      );
    }

    return usersToShow.sort((a, b) => {
      const isFriendA = this.isFriend(a.username);
      const isFriendB = this.isFriend(b.username);

      if (isFriendA && !isFriendB) return 1;

      if (!isFriendA && isFriendB) return -1;

      return a.username.localeCompare(b.username);
    });
  });

  ngOnInit() {
    this.currentUserId = this.authService.currentUser()?.id;

    this.isLoading.set(true);

    forkJoin({
      friends: this.userService.getFriends(this.currentUserId),
      all: this.userService.getAllUsers()
    }).subscribe({
      next: (res) => {
        this.friends.set(res.friends);
        this.allUsers.set(res.all); // Salvăm toți userii pentru căutare
        this.isLoading.set(false);
      },
      error: () => {
        this.show('Error loading data', 'error');
        this.isLoading.set(false);
      }
    });
  }

  isFriend(username: string): boolean {
    return this.friends().some(f => f.username === username);
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

  onInputFocus() {
    this.isSearchFocused.set(true);
  }

  onInputBlur() {
    setTimeout(() => {
      this.isSearchFocused.set(false);
    }, 200);
  }

  /**
   * Social Action: addFriend
   * Persists a new friendship relation. On success, it clears the search input
   * and triggers a reactive refresh of the friend list.
   */
  addFriend(targetUsername: string) {

    if (this.isFriend(targetUsername)) {
      return this.show("You are already friends with this user.", 'error');
    }

    this.userService.addFriend(this.currentUserId, targetUsername).subscribe({
      next: () => {
        this.show("Friend added successfully!", 'success'); // Sau "Friend added"
        this.friendUsername.set(''); // Curăță search-ul

        // Refresh la lista de prieteni
        this.userService.getFriends(this.currentUserId).subscribe(updated => {
          this.friends.set(updated);
        });
      },
      error: (err) => {
        this.show(err.error?.message || "User not found", 'error');
      }
    });
  }

  private show(msg: string, type: 'success' | 'error') {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    this.showToast.set(true);
    setTimeout(() => this.showToast.set(false), 3000);
  }

  selectedFriendProfile = signal<UserProfile | null>(null);
  isModalOpen = signal(false);
  isModalLoading = signal(false);

  /**
   * Modal Logic: viewFriendProfile
   * Fetches detailed profile data (including badges and stats) for a specific user.
   * Uses a loading signal to manage the UserQuickView state.
   */
  viewFriendProfile(friendId: string) {
    this.isModalOpen.set(true);
    this.isModalLoading.set(true);
    this.selectedFriendProfile.set(null);

    this.userService.getUserProfileById(friendId).subscribe({
      next: (profile) => {
        this.selectedFriendProfile.set(profile);
        this.isModalLoading.set(false);
      },
      error: () => {
        this.show("Could not load friend's profile", 'error');
        this.closeModal();
      }
    });
  }

  closeModal() {
    this.isModalOpen.set(false);
    // Opțional resetăm profilul după ce se termină animația de închidere
    setTimeout(() => this.selectedFriendProfile.set(null), 200);
  }
}
