import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Challenge } from '../../models/challenge.model';
import { LucideAngularModule, CheckCircle, RotateCcw } from 'lucide-angular';

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
  @Input() categoryClass: string = 'bg-gray-500'; // Valoare primită de la părinte

  @Output() start = new EventEmitter<Challenge>();
  @Output() restart = new EventEmitter<Challenge>();
  @Output() assign = new EventEmitter<Challenge>();
  @Output() edit = new EventEmitter<Challenge>();
  @Output() delete = new EventEmitter<{event: MouseEvent, challenge: Challenge}>();

  readonly icons = { CheckCircle, RotateCcw };
}
