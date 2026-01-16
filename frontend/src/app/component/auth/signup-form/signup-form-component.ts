import { Component, EventEmitter, Output, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl, ValidationErrors, FormGroup } from '@angular/forms';

/**
 * Component responsible for the user registration process.
 * Advanced implementation using Angular Reactive Forms that features
 * cross-field validation and real-time password complexity tracking.
 * * Key Technical Aspects:
 * - Cross-Field Validation: Custom validator ensures 'password' and 'confirmation' match.
 * - Reactive State: Subscribes to value changes to update visual security criteria.
 * - Secure Payload: Strips the confirmation field before emitting the final data.
 */

/**
 * Custom validator to verify if password and confirmation match.
 * Applied at the FormGroup level to access both controls simultaneously.
 */
function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const formGroup = control as FormGroup;
  const password = formGroup.get('password')?.value;
  const confirmPassword = formGroup.get('passwordConfirmation')?.value;

  if (password && confirmPassword && password !== confirmPassword) {
    return { 'passwordMismatch': true };
  }
  return null;
}

@Component({
  selector: 'app-signup-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './signup-form-component.html',
  styleUrl: '../auth-component.css'
})
export class SignupFormComponent implements OnInit {
  /** Emits the user registration data (minus confirmation) to the parent auth container. */
  @Output() signupRequest = new EventEmitter<any>();
  /** Switches view back to Login mode. */
  @Output() toggleMode = new EventEmitter<void>();

  private fb = inject(FormBuilder);

  /** Tracks the state of password requirements for real-time UI feedback. */
  passwordCriteria = {
    length: false,
    upper: false,
    lower: false,
    digit: false,
    symbol: false
  };

  /**
   * Main FormGroup definition with nested validators:
   * - Username: Required, min 3 chars.
   * - Email: Standard email format validation.
   * - Password: Regex-based complexity (Digit, Lower, Upper, Special, min 6).
   */
  signupForm: FormGroup = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [
      Validators.required,
      Validators.pattern('^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()\\-_+=<>?/{}\\[\\]|:;\"\'.,~`]).{6,}$')
    ]],
    passwordConfirmation: ['', [Validators.required]],
  }, { validators: passwordMatchValidator });

  // Getters for template abstraction
  get username() { return this.signupForm.get('username'); }
  get email() { return this.signupForm.get('email'); }
  get password() { return this.signupForm.get('password'); }
  get passwordConfirmation() { return this.signupForm.get('passwordConfirmation'); }

  ngOnInit(): void {
    /** Reactive subscription to monitor password typing and update criteria checklist. */
    this.signupForm.get('password')?.valueChanges.subscribe((value) => {
      this.updatePasswordCriteria(value || '');
    });
  }

  /** Uses Regex tests to evaluate password strength. */
  private updatePasswordCriteria(value: string) {
    this.passwordCriteria = {
      length: value.length >= 6,
      upper: /[A-Z]/.test(value),
      lower: /[a-z]/.test(value),
      digit: /[0-9]/.test(value), // Aici era eroarea TS2322 (lipsea apelul functiei .test(value))
      symbol: /[!@#$%^&*()\-_+=<>?/{}[\]|:;"'.,~`]/.test(value)
    };
  }

  /** Handles form submission, validating state and stripping unnecessary fields. */
  onSubmit() {
    if (this.signupForm.valid) {
      const { passwordConfirmation, ...userPayload } = this.signupForm.value;
      this.signupRequest.emit(userPayload);
    } else {
      this.signupForm.markAllAsTouched();
    }
  }
}
