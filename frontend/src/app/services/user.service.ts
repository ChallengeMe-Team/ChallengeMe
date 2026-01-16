import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {UserProfile} from '../models/user.model';

export interface FriendDTO {
  id: any;
  username: string;
  points: number;
  avatar?: string;
}

export interface UserDTO {
  id: string;
  username: string;
  email: string;
  points: number;
  avatar?: string;
  role?: string;
}

/**
 * A singleton service managing user-centric data, social relationships,
 * and profile customization.
 * * * Key Technical Aspects:
 * - Social Integration: Manages many-to-many relationship queries for friend lists.
 * - Identity Verification: Provides real-time availability checks for registration
 * and profile updates.
 * - Data Synchronization: Handles complex user updates that require JWT
 * token refreshes.
 */
@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/users`;

  /**
   * Retrieves the comprehensive profile of the currently authenticated user.
   * Logic: Relies on the AuthInterceptor to provide the JWT for identification.
   * @returns An Observable of UserProfile containing stats, activity, and badges.
   */
  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/profile`);
  }

  /**
   *Retrieves the social graph for a specific user.
   * @param userId The unique identifier of the target user.
   * @returns An Observable stream of FriendDTOs (compact user objects).
   */
  getFriends(userId: string): Observable<FriendDTO[]> {
    return this.http.get<FriendDTO[]>(`${this.apiUrl}/${userId}/friends`);
  }

  /**
   * Performs a query-based search for a user by their exact username.
   */
  searchUser(username: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/search?username=${username}`);
  }

  /**
   * Establishes a bidirectional friendship link between two users in the database.
   */
  addFriend(currentUserId: string, username: string): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/${currentUserId}/friends?username=${username}`,
      {}
    );
  }

  getAllUsers(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(this.apiUrl);
  }

  /**
   * Performs a partial or full update of user metadata (avatar, email, etc.).
   * * Important Logic: Because identity changes (like email) affect JWT payload,
   * this endpoint returns both the updated User and a new Auth Token.
   */
  updateUser(id: string, data: Partial<UserDTO>): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, data);
  }

  /**
   * Submits a secure password change request.
   * @param payload Contains { currentPassword, newPassword }.
   */
  changePassword(id: string, payload: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/password`, payload);
  }

  /**
   * Real-time verification methods used during registration or settings updates
   * to ensure uniqueness of credentials in the database.
   */
  checkUsername(username: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/check-username?username=${username}`);
  }

  checkEmail(email: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/check-email?email=${email}`);
  }

  /** Retrieves profile details for a specific ID, used for viewing friend/other profiles. */
  getUserProfileById(userId: string): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/${userId}/profile`);
  }
}
