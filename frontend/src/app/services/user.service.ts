import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface FriendDTO {
    id: any;
    username: string;
    points: number;
    avatarUrl?: string; // Opțional, pentru UI
}

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/users`;

    getFriends(userId: string): Observable<FriendDTO[]> {
        return this.http.get<FriendDTO[]>(`${this.apiUrl}/users/${userId}/friends`);
    }
}
