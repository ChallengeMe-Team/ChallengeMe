import { Pipe, PipeTransform } from '@angular/core';

/**
 * A pure standalone pipe designed to calculate the elapsed time between
 * a system event and the current moment.
 * * * Key Technical Aspects:
 * - Data Normalization: Specifically engineered to handle polymorphic inputs,
 * converting Java LocalDateTime arrays or ISO strings into native JS Date objects.
 * - Time Arithmetic: Uses Unix Epoch timestamps (milliseconds) to calculate
 * the precise delta used for interval determination.
 * - Pluralization Logic: Automatically handles grammatical singular/plural
 * suffixes for all time units (e.g., "1 minute" vs "5 minutes").
 */
@Pipe({
  name: 'timeAgo',
  standalone: true
})
export class TimeAgoPipe implements PipeTransform {

  /**
   * The primary transformation engine. It parses the input value and iterates
   * through predefined time constants to find the most significant unit.
   * @param value A Date object, ISO string, or a Java [YYYY, MM, DD, ...] array.
   * @returns A formatted string (e.g., "2 hours ago").
   */
  transform(value: string | number[] | Date): string {
    if (!value) return '';

    console.log('DEBUG PIPE - Valoare primită:', value); //

    let date: Date;

    // DATA NORMALIZATION
    // Handles Java backend array formats specifically to prevent timezone/parsing errors
    if (Array.isArray(value)) {
      date = new Date(value[0], value[1] - 1, value[2], value[3] || 0, value[4] || 0, value[5] || 0);
    } else {
      date = new Date(value);
    }

    const now = new Date();
    // Delta calculation in seconds
    const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);

    // THRESHOLD LOGIC
    // Prevents "in the future" or jittery values for events occurring within the last 30 seconds
    if (seconds < 30) return 'Just now';

    /** * CONSTANT INTERVALS: Define the weight of each unit in seconds.
     * Used for the downward comparison loop.
     */
    const intervals: { [key: string]: number } = {
      'year': 31536000,
      'month': 2592000,
      'week': 604800,
      'day': 86400,
      'hour': 3600,
      'minute': 60,
      'second': 1
    };

    // UNIT DETERMINATION LOOP
    for (const i in intervals) {
      const counter = Math.floor(seconds / intervals[i]);
      if (counter > 0) {
        // Grammatical pluralization
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
