// friends-list.component.ts

import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FriendDTO, UserService } from '../../../services/user.service';

// ------------------------------------------------------------------
// DATE HARDCODATE PENTRU TESTARE (MOCK DATA)
// ------------------------------------------------------------------
const MOCK_FRIENDS: FriendDTO[] = [
  { id: 'mock-1', username: 'Andrei_Coder', points: 8750 },
  { id: 'mock-2', username: 'Elena_UX', points: 6420 },
  { id: 'mock-3', username: 'Mihai_DevOps', points: 4980 },
  { id: 'mock-4', username: 'Sofia_Tester', points: 3100 },
  { id: 'mock-5', username: 'Gigi_The_CodeMaster', points: 5500 },
];
// ------------------------------------------------------------------

@Component({
  selector: 'app-friends-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './friends-list.component.html',
  styleUrl: './friends-list.component.css'
})
export class FriendsListComponent implements OnInit {
  private userService = inject(UserService);

  private currentUserId = '1373b29e-d23c-45a4-85b8-21d1d7da2495';

  //Inițializăm friends direct cu datele hardcodate
  friends = signal<FriendDTO[]>(MOCK_FRIENDS);
  //Setăm isLoading pe false, deoarece nu mai așteptăm răspunsul API
  isLoading = signal<boolean>(false);

  ngOnInit() {
    // Am comentat sau șters apelul la funcția care cheamă API-ul,
    // pentru a folosi datele MOCK.
    // this.loadFriends();
  }

  // Păstrăm funcția loadFriends, dar nu o mai apelăm în ngOnInit.
  loadFriends() {
    this.isLoading.set(true);
    this.userService.getFriends(this.currentUserId).subscribe({
      next: (data) => {
        this.friends.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error loading friends', err);
        this.isLoading.set(false);
      }
    });
  }
}
