import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FriendDTO, UserService } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import {ToastComponent} from '../../../shared/toast/toast-component';
import {FormsModule} from '@angular/forms';
import { computed } from '@angular/core';
import { forkJoin } from 'rxjs';
import { UserDTO } from '../../../services/user.service';

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

  allUsers = signal<UserDTO[]>([]);

  // For the "Add Friend" input
  friendUsername = signal('');

  // Toast state
  toastMessage = signal('');
  toastType = signal<'success'|'error'>('success');
  showToast = signal(false);

  // Variabilă pentru a ști dacă utilizatorul a dat click în search bar
  isSearchFocused = signal(false);


  // Computed Signal pentru filtrare (Dropdown logic)
  // Returnează userii care conțin textul din input (nume sau email)
  // ȘI exclude userul curent (nu te poți adăuga pe tine)
  filteredUsers = computed(() => {
    const search = this.friendUsername().toLowerCase().trim();
    const all = this.allUsers();
    const currentUser = this.authService.currentUser()?.username;

    // Excludem userul curent din start
    let usersToShow = all.filter(u => u.username !== currentUser);

    // Dacă avem search, filtrăm lista. Dacă nu, rămâne lista completă (usersToShow)
    if (search) {
      usersToShow = usersToShow.filter(u =>
        u.username.toLowerCase().includes(search) || u.email?.toLowerCase().includes(search)
      );
    }

    // SORTARE: Cei care NU sunt prieteni apar primii, prietenii apar la urmă
    return usersToShow.sort((a, b) => {
      const isFriendA = this.isFriend(a.username);
      const isFriendB = this.isFriend(b.username);

      // Dacă A e prieten și B nu e => A merge jos (return 1)
      if (isFriendA && !isFriendB) return 1;

      // Dacă A nu e prieten și B e => A merge sus (return -1)
      if (!isFriendA && isFriendB) return -1;

      // Dacă amândoi sunt la fel (ambii prieteni sau ambii nu), îi ordonăm alfabetic
      return a.username.localeCompare(b.username);
    });
  });

  ngOnInit() {
    this.currentUserId = this.authService.currentUser()?.id;

    this.isLoading.set(true);

    // Folosim forkJoin pentru eficiență
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

  //  Helper pentru a verifica rapid dacă e prieten (pentru UI)
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

  // Metode pentru a gestiona focus-ul (cu mic delay la blur ca să apuci să dai click pe listă)
  onInputFocus() {
    this.isSearchFocused.set(true);
  }

  onInputBlur() {
    // Delay mic pentru a permite click-ul pe dropdown înainte să dispară
    setTimeout(() => {
      this.isSearchFocused.set(false);
    }, 200);
  }

  // ADD FRIEND FLOW
  // am modificat addFriend să primească username direct din Dropdown
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

  // Toast helper
  private show(msg: string, type: 'success' | 'error') {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    this.showToast.set(true);
    setTimeout(() => this.showToast.set(false), 3000);
  }
}
