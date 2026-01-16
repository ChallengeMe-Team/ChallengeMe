import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { UserProfile } from '../../../models/user.model';
import { BadgeComponent } from '../../badge/badge.component';
import { LucideAngularModule, Settings, Zap, Trophy, CheckCircle } from 'lucide-angular';
import { TimeAgoPipe } from '../../../pipes/time-ago.pipe';

/**
 * Core component for rendering the user's identity, gamification progress,
 * and historical activity.
 * * * Key Technical Aspects:
 * - Signal-based State: Uses 'signal' for fine-grained reactivity when
 * updating profile data or XP bars.
 * - Temporal Logic: Implements custom timestamp parsing to handle backend
 * Date arrays (LocalDateTime) vs ISO strings.
 * - Visual Semantic Mapping: Centralizes color logic for quest categories to
 * maintain brand consistency across the UI.
 */
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

  /** Signal containing the comprehensive user profile object (stats, badges, activity). */
  profile = signal<UserProfile | null>(null);

  /** Boolean flag to manage the global loading splash screen. */
  isLoading = signal(true);

  /** Centralized Lucide icon mapping for profile-specific sections. */
  readonly icons = { Settings, Zap, Trophy, CheckCircle };

  /**
   * Triggers the primary profile data fetch. Includes a post-fetch sorting
   * logic to ensure the 'Recent Activity' feed is displayed in strict
   * reverse-chronological order (newest first).
   */
  ngOnInit() {
    this.userService.getProfile().subscribe({
      next: (data) => {
        console.log('DEBUG FRONTEND - Date profil primite:', data); //
        if (data && data.recentActivity && data.recentActivity.length > 0) {
          // Chronological sorting of activity feed
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

  /**
   * Normalizes backend date formats. Handles both standard ISO strings and
   * LocalDateTime arrays [YYYY, MM, DD, HH, MM, SS] into JavaScript Date objects.
   * @param ts The raw timestamp from the API.
   * @returns A native JS Date object.
   */
  private parseTimestamp(ts: any): Date {
    if (Array.isArray(ts)) {
      // Maps Java LocalDateTime array indices to JS Date constructor
      return new Date(ts[0], ts[1] - 1, ts[2], ts[3], ts[4], ts[5] || 0);
    }
    return new Date(ts);
  }

  /**
   * Maps string categories to specific Hex color codes. Used for visual
   * identification of activities in the feed.
   * @param category The string name of the category (e.g., 'Coding').
   * @returns A Hex string for CSS styling.
   */
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

  /**
   * Derives the visual progress for the XP bar based on a "100 XP per Level" rule.
   * Uses the modulo operator to determine the remainder of current level progress.
   */
  get progressPercent(): number {
    const points = this.profile()?.points || 0;
    return points % 100;
  }
}
