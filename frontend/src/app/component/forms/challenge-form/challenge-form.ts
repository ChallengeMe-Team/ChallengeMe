import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, Output, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

/**
 * Component responsible for creating and editing challenges.
 * It includes a client-side content moderation system that filters
 * inappropriate language using an external dictionary.
 * * * Key Features:
 * - Dual Mode: Supports both 'create' and 'edit' operations via @Input properties.
 * - Content Guard: Dynamically loads a list of forbidden words and validates input fields.
 * - Reactive Validation: Real-time feedback and state-based button disabling.
 * - Data Mapping: Uses the spread operator to ensure immutability when editing existing data.
 */
@Component({
  selector: 'app-challenge-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './challenge-form.html',
  styleUrls: ['./challenge-form.css']
})
export class ChallengeFormComponent implements OnInit{
/** Injected HttpClient for loading local asset files (dictionary). */
  private http = inject(HttpClient);

  /** Internal list of forbidden words used for content moderation. */
  forbiddenWords: string[] = [];

  ngOnInit() {
    this.loadForbiddenWords();

    if (this.mode === 'edit' && this.challengeData) {
      this.challenge = { ...this.challengeData };
    }
  }

  /** Determines the UI state and labeling of the form. */
  @Input() mode: 'create' | 'edit' = 'create';

  /** The existing challenge data object if the mode is 'edit'. */
  @Input() challengeData: any = null;

  /** Notifies the parent to close the modal. */
  @Output() cancel = new EventEmitter<void>();

  /** Emits the validated challenge object back to the service layer. */
  @Output() submitChallenge = new EventEmitter<any>();

  /** The local model for the challenge entity. */
  challenge = {
    id: null,
    title: '',
    description: '',
    category: '',
    difficulty: '',
    points: 100
  };

  /** Specific error messages for visual feedback. */
  errors = {
    title: '',
    description: ''
  };

  /** Preset difficulty options for the dropdown selector. */
  difficulties = ['EASY', 'MEDIUM', 'HARD'];

  onSubmit() {
    this.validateField('title');
    this.validateField('description');

    if (this.errors.title || this.errors.description) {
      return;
    }

    if (!this.challenge.title || !this.challenge.description || !this.challenge.category || !this.challenge.difficulty) {
      alert('Please fill out all required fields!');
      return;
    }

    this.submitChallenge.emit(this.challenge);
  }

  /**
   * Fetches the 'bad-words.txt' file from assets and parses it into an array.
   * This provides a configurable, externalized dictionary for moderation.
   */
  private loadForbiddenWords() {
    this.http.get('assets/bad-words.txt', { responseType: 'text' }).subscribe({
      next: (data) => {
        this.forbiddenWords = data.split(/\r?\n/)
          .map(word => word.trim().toLowerCase())
          .filter(word => word.length > 0);

        console.log('Cuvinte încărcate:', this.forbiddenWords.length);
      }
    });
  }

  /**
   * Performs real-time scanning of input fields against the forbidden dictionary.
   * @param field The property name (title or description) to validate.
   */
  validateField(field: 'title' | 'description') {
    const value = this.challenge[field];
    if (!value || this.forbiddenWords.length === 0) {
      this.errors[field] = '';
      return;
    }

    const lowerInput = value.toLowerCase();

    const hasBadContent = this.forbiddenWords.some(word => lowerInput.includes(word));

    this.errors[field] = hasBadContent ? 'Inappropriate content detected!' : '';
  }

  /**
   * Computed getter for form state.
   * Ensures the button is locked if fields are empty or contain errors.
   */
  get isFormInvalid(): boolean {
    return !!this.errors.title ||
      !!this.errors.description ||
      !this.challenge.title.trim() ||
      !this.challenge.category.trim() ||
      !this.challenge.difficulty;
  }
}
