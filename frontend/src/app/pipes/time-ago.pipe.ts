import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeAgo',
  standalone: true
})
export class TimeAgoPipe implements PipeTransform {

  transform(value: string | number[] | Date): string {
    if (!value) return '';

    let date: Date;

    // VERIFICARE: Dacă vine Array de la Java [an, luna, zi, ora, min...]
    if (Array.isArray(value)) {
      // Atenție: În JS lunile sunt 0-11, Java trimite 1-12. Scădem 1 la lună.
      // Format: [year, month, day, hour, minute, second]
      date = new Date(
        value[0],      // Year
        value[1] - 1,  // Month (Java 1 = Jan, JS 0 = Jan)
        value[2],      // Day
        value[3] || 0, // Hour
        value[4] || 0, // Minute
        value[5] || 0  // Second
      );
    } else {
      // Dacă e string ISO sau Date object
      date = new Date(value);
    }

    const now = new Date();
    const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);

    // Logică afișare text
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
