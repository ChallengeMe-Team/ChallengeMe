import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface FriendDTO {
  id: any;
  username: string;
  points: number;
  avatar?: string;
}

export interface UserDTO {
  id: string;
  username: string;
  email: string;       // email pentru search
  points: number;
  avatar?: string;
  role?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/users`;

  // GET friends of user
  getFriends(userId: string): Observable<FriendDTO[]> {
    return this.http.get<FriendDTO[]>(`${this.apiUrl}/${userId}/friends`);
  }

  // SEARCH user by username
  searchUser(username: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/search?username=${username}`);
  }

  // ADD a friend
  addFriend(currentUserId: string, username: string): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/${currentUserId}/friends?username=${username}`,
      {}
    );
  }

  getAllUsers(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(this.apiUrl);
  }

  updateUser(id: string, data: Partial<UserDTO>): Observable<UserDTO> {
    return this.http.put<UserDTO>(`${this.apiUrl}/${id}`, data);
  }
}
