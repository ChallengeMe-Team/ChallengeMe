import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeAgo',
  standalone: true
})
export class TimeAgoPipe implements PipeTransform {

  transform(value: string | number[] | Date): string {
    if (!value) return '';

    console.log('DEBUG PIPE - Valoare primită:', value); //

    let date: Date;

    // VERIFICARE: Dacă vine Array de la Java [an, luna, zi, ora, min...]
    if (Array.isArray(value)) {
      // Verifică log-ul: dacă Java trimite [2026, 1, 15, 19, 46...], JS va crede că e ora locală
      date = new Date(value[0], value[1] - 1, value[2], value[3] || 0, value[4] || 0, value[5] || 0);
    } else {
      // Dacă e string ISO (ca în log: 2026-01-15T19:36...), asigură-te că JS îl vede corect
      date = new Date(value);
    }

    const now = new Date();

    console.log('DEBUG PIPE - Ora curentă a browserului:', now);

    const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);

    console.log('DEBUG PIPE - Secunde calculate între acum și dată:', seconds);

    // Logică afișare text
    if (seconds < 0) return 'Just now';
    if (seconds < 30) return 'Just now';

    const intervals: { [key: string]: number } = {
      'year': 31536000,
      'month': 2592000,
      'week': 604800,
      'day': 86400,
      'hour': 3600,
      'minute': 60,
      'second': 1
    };

    for (const i in intervals) {
      const counter = Math.floor(seconds / intervals[i]);
      if (counter > 0) {
        if (counter === 1) {
          return counter + ' ' + i + ' ago'; // ex: 1 minute ago
        } else {
          return counter + ' ' + i + 's ago'; // ex: 5 minutes ago
        }
      }
    }

    return 'Just now';
  }
}
