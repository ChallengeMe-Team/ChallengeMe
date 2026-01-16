import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup, AbstractControl } from '@angular/forms';

/**
 * Component responsible for the user login interface.
 * Utilizes Angular Reactive Forms for robust state management and field validation.
 * * * Key Features:
 * - Reactive validation for mandatory fields.
 * - Decoupled logic: emits events instead of handling API calls directly.
 * - Safe template access via dedicated getters for form controls.
 */
@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login-form-component.html',
  styleUrl: '../auth-component.css',
})
export class LoginFormComponent {
  /** Emits the form credentials to the parent component for API processing. */
  @Output() loginRequest = new EventEmitter<any>();

  /** Notifies the parent to switch the view from Login to Registration mode. */
  @Output() toggleMode = new EventEmitter<void>();

  /** Injecting FormBuilder for concise reactive form definition. */
  private fb = inject(FormBuilder);

  /** * Form structure definition with built-in validators.
   * Fields:
   * - emailOrUsername: string (Required)
   * - password: string (Required)
   */
  loginForm: FormGroup = this.fb.group({
    emailOrUsername: ['', Validators.required],
    password: ['', Validators.required]
  });

  /**
   * Handles the form submission.
   * Triggers visual validation feedback by marking all fields as touched.
   * Only emits the request if the form state is valid.
   */
  onSubmit() {
    this.loginForm.markAllAsTouched();
    if (this.loginForm.valid) {
      this.loginRequest.emit(this.loginForm.value);
    }
  }

  // --- GETTERS ---
  /** Returns the 'emailOrUsername' control for template validation checks. */
  get emailOrUsername(): AbstractControl | null { return this.loginForm.get('emailOrUsername'); }
  /** Returns the 'password' control for template validation checks. */
  get password(): AbstractControl | null { return this.loginForm.get('password'); }
}
