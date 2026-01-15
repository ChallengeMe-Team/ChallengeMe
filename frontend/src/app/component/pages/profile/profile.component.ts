import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { UserProfile } from '../../../models/user.model';
import { BadgeComponent } from '../../badge/badge.component';
import { LucideAngularModule, Settings, Zap, Trophy, CheckCircle } from 'lucide-angular';
import { TimeAgoPipe } from '../../../pipes/time-ago.pipe';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, BadgeComponent, TimeAgoPipe],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  private userService = inject(UserService);
  public router = inject(Router);

  profile = signal<UserProfile | null>(null);
  isLoading = signal(true);

  readonly icons = { Settings, Zap, Trophy, CheckCircle };

  ngOnInit() {
    this.userService.getProfile().subscribe({
      next: (data) => {
        console.log('DEBUG FRONTEND - Date profil primite:', data); //
        if (data && data.recentActivity && data.recentActivity.length > 0) {
          data.recentActivity.sort((a, b) => {
            if (!a.date || !b.date) return 0; // Siguranță pentru date null
            return this.parseTimestamp(b.date).getTime() - this.parseTimestamp(a.date).getTime();
          });
        }
        this.profile.set(data);
        this.isLoading.set(false);
      }
    });
  }

  private parseTimestamp(ts: any): Date {
    if (Array.isArray(ts)) {
      return new Date(ts[0], ts[1] - 1, ts[2], ts[3], ts[4], ts[5] || 0);
    }
    return new Date(ts);
  }

  getCategoryColor(category: string): string {
    const colors: { [key: string]: string } = {
      'Fitness': '#d946ef',    // Fuchsia
      'Food': '#f59e0b',       // Chihlimbar
      'Health': '#ef4444',     // Roșu
      'Mindfulness': '#06b6d4', // Cyan
      'Education': '#8b5cf6',  // Mov
      'Creativity': '#ec4899', // Roz
      'Coding': '#6366f1',     // Indigo
      'Lifestyle': '#10b981',  // Verde
      'Social': '#f97316'      // Portocaliu
    };
    return colors[category] || '#6b7280'; // Gri default
  }

  // XP Progress (Exemplu: 100 XP per Level)
  get progressPercent(): number {
    const points = this.profile()?.points || 0;
    return points % 100;
  }
}
