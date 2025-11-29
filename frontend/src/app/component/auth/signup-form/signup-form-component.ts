import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl, ValidationErrors, FormGroup } from '@angular/forms';

/**
 * Validator personalizat pentru a verifica dacă parola și confirmarea parolei coincid.
 */
function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const formGroup = control as FormGroup;
  const password = formGroup.get('password')?.value;
  const confirmPassword = formGroup.get('passwordConfirmation')?.value;

  // Dacă ambele câmpuri au valori și nu sunt egale, returnăm eroarea
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
export class SignupFormComponent {
  // Evenimente emise către componenta părinte (AuthContainer)
  @Output() signupRequest = new EventEmitter<any>();
  @Output() toggleMode = new EventEmitter<void>();

  private fb = inject(FormBuilder);

  // Definirea formularului cu validatori
  signupForm: FormGroup = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    passwordConfirmation: ['', Validators.required]
  }, { validators: passwordMatchValidator });

  // Getteri pentru a accesa ușor controalele în HTML (evită erorile de tip 'private member')
  get username() { return this.signupForm.get('username'); }
  get email() { return this.signupForm.get('email'); }
  get password() { return this.signupForm.get('password'); }
  get passwordConfirmation() { return this.signupForm.get('passwordConfirmation'); }

  // Metoda apelată la submit
  onSubmit() {
    if (this.signupForm.valid) {
      // Extragem doar datele necesare pentru backend (UserCreateRequest)
      // Excludem câmpul 'passwordConfirmation' care nu există în modelul de backend
      const { passwordConfirmation, ...userPayload } = this.signupForm.value;

      // Emitem datele către containerul părinte pentru a face request-ul
      this.signupRequest.emit(userPayload);
    } else {
      // Marcăm toate câmpurile ca "atinse" pentru a afișa erorile de validare în UI
      this.signupForm.markAllAsTouched();
    }
  }
}
