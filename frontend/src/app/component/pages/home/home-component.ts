import {Component, ChangeDetectionStrategy, Output, EventEmitter} from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import {HeroSectionComponent} from '../../hero-section/hero-section.component';
import { StatsGridComponent } from '../../stats-grid/stats-grid.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, HeroSectionComponent, StatsGridComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './home-component.html',
  styleUrls: ['./home-component.css']
})
export class HomeComponent {
  @Output() startChallengeRequest = new EventEmitter<void>();

  onStartChallenge() {
    this.startChallengeRequest.emit();
  }

  @Output() exploreChallenges = new EventEmitter<void>();

  onExploreChallenges() {
    this.exploreChallenges.emit();
  }

  stats: { icon: SafeHtml; label: string; value: string; color: string; bgColor: string; }[];

  constructor(private sanitizer: DomSanitizer) {
    const svg = (code: string) => this.sanitizer.bypassSecurityTrustHtml(code);

    const TrophyIcon = svg(`<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
      fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
      class="lucide lucide-trophy"><path d="M8 21h8"/><path d="M12 17v4"/><path d="M7 4v3a5 5 0 0 0 10 0V4"/>
      <path d="M17 8a5 5 0 0 0 5-5H2a5 5 0 0 0 5 5"/></svg>`);

    const TargetIcon = svg(`<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
      fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
      class="lucide lucide-target"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/></svg>`);

    const TrendingUpIcon = svg(`<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
      fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
      class="lucide lucide-trending-up"><polyline points="22 7 13.5 15.5 8.5 10.5 2 17"/><polyline points="16 7 22 7 22 13"/></svg>`);

    const AwardIcon = svg(`<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
      fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
      class="lucide lucide-award"><circle cx="12" cy="8" r="7"/><path d="M8.21 13.89L7 23l5-3 5 3-1.21-9.11"/></svg>`);

    this.stats = [
      { icon: TrophyIcon, label: 'Total Points', value: '1,250', color: 'text-yellow-400', bgColor: '#3b2f56' },
      { icon: TargetIcon, label: 'Challenges Completed', value: '18', color: 'text-green-400', bgColor: '#1f4738' },
      { icon: TrendingUpIcon, label: 'Current Streak', value: '7 days', color: 'text-orange-400', bgColor: '#5e4b2d' },
      { icon: AwardIcon, label: 'Badges Earned', value: '5', color: 'text-pink-400', bgColor: '#5a274a' }
    ];
  }
  trackByLabel(index: number, item: any) {
    return item.label;
  }
}
