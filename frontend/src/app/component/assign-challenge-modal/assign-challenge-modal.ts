import { Component, EventEmitter, Input, Output, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, FriendDTO } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';

/**
 * Component responsible for selecting a friend to assign a specific challenge.
 * It features a real-time search/filter mechanism and integrates with the UserService
 * to fetch the user's social circle.
 * * Key Logic:
 * - Uses Angular Signals for efficient state management (searchQuery, friends list).
 * - Implements computed properties for real-time filtering of the friend list.
 * - Handles avatar fallback logic (initials) when profile images are missing.
 */
@Component({
  selector: 'app-assign-challenge-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assign-challenge-modal.html',
  styleUrls: ['./assign-challenge-modal.css']
})
export class AssignChallengeModalComponent implements OnInit {
  /** Service injection for user data and authentication state */
  private userService = inject(UserService);
  private auth = inject(AuthService);

  /** Visibility toggle for the modal overlay */
  @Input() isVisible: boolean = false;
  /** Event emitted when the user closes the modal without selecting anyone */
  @Output() close = new EventEmitter<void>();
  /** Event emitted when a friend is selected, returning the friend's data to the parent */
  @Output() friendSelected = new EventEmitter<FriendDTO>();

  /** Reactive state for the list of friends */
  friends = signal<FriendDTO[]>([]);
  /** Reactive state for the search input value */
  searchQuery = signal('');

  /**
   * Computed signal that returns a filtered list of friends
   * based on the current search query (case-insensitive).
   */
  filteredFriends = computed(() => {
    const query = this.searchQuery().toLowerCase();
    return this.friends().filter(f => f.username.toLowerCase().includes(query));
  });

  /**
   * Lifecycle hook to populate the friends list on initialization.
   * Requires the current authenticated user's ID.
   */
  ngOnInit() {
    const user = this.auth.currentUser();
    if (user) {
      this.userService.getFriends(user.id).subscribe(data => this.friends.set(data));
    }
  }

  /**
   * Generates a 2-character uppercase string for avatar placeholders.
   * @param name The username to process.
   */
  getInitials(name: string): string {
    if (!name) return '';
    return name.slice(0, 2).toUpperCase();
  }

  /**
   * Finalizes the selection process by emitting the chosen friend.
   * @param friend The selected FriendDTO object.
   */
  onSelect(friend: FriendDTO) {
    this.friendSelected.emit(friend);
  }
}
