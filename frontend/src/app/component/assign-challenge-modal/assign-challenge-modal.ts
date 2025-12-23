import { Component, EventEmitter, Input, Output, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, FriendDTO } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-assign-challenge-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assign-challenge-modal.html',
  styleUrls: ['./assign-challenge-modal.css']
})
export class AssignChallengeModalComponent implements OnInit {
  private userService = inject(UserService);
  private auth = inject(AuthService);

  @Input() isVisible: boolean = false;
  @Output() close = new EventEmitter<void>();
  @Output() friendSelected = new EventEmitter<FriendDTO>();

  friends = signal<FriendDTO[]>([]);
  searchQuery = signal('');

  filteredFriends = computed(() => {
    const query = this.searchQuery().toLowerCase();
    return this.friends().filter(f => f.username.toLowerCase().includes(query));
  });

  ngOnInit() {
    const user = this.auth.currentUser();
    if (user) {
      this.userService.getFriends(user.id).subscribe(data => this.friends.set(data));
    }
  }

  getInitials(name: string): string {
    if (!name) return '';
    return name.slice(0, 2).toUpperCase();
  }

  onSelect(friend: FriendDTO) {
    this.friendSelected.emit(friend);
  }
}
