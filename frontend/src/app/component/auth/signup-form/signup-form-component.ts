import { Component, EventEmitter, Output, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl, ValidationErrors, FormGroup } from '@angular/forms';

/**
 * Validator personalizat pentru a verifica dacă parola și confirmarea parolei coincid.
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
  @Output() signupRequest = new EventEmitter<any>();
  @Output() toggleMode = new EventEmitter<void>();

  private fb = inject(FormBuilder);

  // --- STAREA PENTRU VALIDAREA VIZUALĂ ---
  passwordCriteria = {
    length: false,
    upper: false,
    lower: false,
    digit: false,
    symbol: false
  };

  signupForm: FormGroup = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [
      Validators.required,
      Validators.pattern('^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()\\-_+=<>?/{}\\[\\]|:;\"\'.,~`]).{6,}$')
    ]],
    passwordConfirmation: ['', [Validators.required]],
  }, { validators: passwordMatchValidator });

  get username() { return this.signupForm.get('username'); }
  get email() { return this.signupForm.get('email'); }
  get password() { return this.signupForm.get('password'); }
  get passwordConfirmation() { return this.signupForm.get('passwordConfirmation'); }

  ngOnInit(): void {
    this.signupForm.get('password')?.valueChanges.subscribe((value) => {
      this.updatePasswordCriteria(value || '');
    });
  }

  private updatePasswordCriteria(value: string) {
    this.passwordCriteria = {
      length: value.length >= 6,
      upper: /[A-Z]/.test(value),
      lower: /[a-z]/.test(value),
      digit: /[0-9]/.test(value), // Aici era eroarea TS2322 (lipsea apelul functiei .test(value))
      symbol: /[!@#$%^&*()\-_+=<>?/{}[\]|:;"'.,~`]/.test(value)
    };
  }

  // Aici era eroarea TS2339 (metoda lipsea complet)
  onSubmit() {
    if (this.signupForm.valid) {
      const { passwordConfirmation, ...userPayload } = this.signupForm.value;
      this.signupRequest.emit(userPayload);
    } else {
      this.signupForm.markAllAsTouched();
    }
  }
}
