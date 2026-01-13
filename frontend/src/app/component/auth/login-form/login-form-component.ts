import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup, AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login-form-component.html',
  styleUrl: '../auth-component.css',
})
export class LoginFormComponent {
  @Output() loginRequest = new EventEmitter<any>();
  @Output() toggleMode = new EventEmitter<void>();

  private fb = inject(FormBuilder);

  loginForm: FormGroup = this.fb.group({
    emailOrUsername: ['', Validators.required],
    password: ['', Validators.required]
  });

  onSubmit() {
    this.loginForm.markAllAsTouched();
    if (this.loginForm.valid) {
      this.loginRequest.emit(this.loginForm.value);
    }
  }

  // GETTERI DEDICAȚI PENTRU ACCES USOR ȘI SIGUR ÎN TEMPLATE
  get emailOrUsername(): AbstractControl | null { return this.loginForm.get('emailOrUsername'); }
  get password(): AbstractControl | null { return this.loginForm.get('password'); }
}
