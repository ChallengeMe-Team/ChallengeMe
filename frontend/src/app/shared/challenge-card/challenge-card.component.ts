import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Challenge } from '../../models/challenge.model';
import { LucideAngularModule, CheckCircle, RotateCcw } from 'lucide-angular';
import { getCategoryGradient } from '../utils/color-utils';

@Component({
  selector: 'app-challenge-card',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './challenge-card.component.html',
  styleUrls: ['./challenge-card.component.css']
})
export class ChallengeCardComponent {
  @Input({ required: true }) challenge!: Challenge;
  @Input() status: string | undefined;

  @Output() start = new EventEmitter<Challenge>();
  @Output() restart = new EventEmitter<Challenge>();
  @Output() assign = new EventEmitter<Challenge>();
  @Output() edit = new EventEmitter<Challenge>();
  @Output() delete = new EventEmitter<{event: MouseEvent, challenge: Challenge}>();

  readonly icons = { CheckCircle, RotateCcw };

  // Metoda acum doar apelează utilitarul global
  getCategoryStyle(category: string) {
    return getCategoryGradient(category);
  }
}
