import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-component.html',
  styleUrls: ['./toast-component.css']
})
export class ToastComponent implements OnInit {
  @Input() message = '';
  @Input() type: 'success' | 'error' = 'success';

  visible = false;

  ngOnInit() {
    this.visible = true;
    setTimeout(() => this.visible = false, 3000); // dispare automat după 3 secunde
  }

  get icon() {
    return this.type === 'success' ? '✅' : '❌';
  }

  get gradient() {
    return this.type === 'success'
      ? 'from-indigo-500 to-purple-500'
      : 'from-red-600 to-pink-500';
  }
}
