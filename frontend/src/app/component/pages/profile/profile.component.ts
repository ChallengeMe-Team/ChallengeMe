import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { UserProfile } from '../../../models/user.model';
import { BadgeComponent } from '../../badge/badge.component';
import { LucideAngularModule, Settings, Zap, Trophy, CheckCircle } from 'lucide-angular';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, BadgeComponent],
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
        this.profile.set(data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  // XP Progress (Exemplu: 100 XP per Level)
  get progressPercent(): number {
    const points = this.profile()?.points || 0;
    return points % 100;
  }
}
