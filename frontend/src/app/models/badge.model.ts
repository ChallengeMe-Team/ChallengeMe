/** * These interfaces define the data contract for the gamification engine.
 * They ensure consistency between the Backend DTOs and Frontend components
 * like BadgeCard and BadgeShowcase.
 */

/*** ----------------
 * Represents the master definition of an achievement stored in the database.
 */
export interface Badge {
  /** The specific requirement or quest goal needed to earn this award (e.g., 'Complete 5 Fitness Quests'). */
  criteria: string;

  /** Unique UUID or String identifier for database lookups and link associations. */
  id: string;

  /** The public-facing name of the achievement (e.g., 'Early Bird', 'Coding Ninja'). */
  name: string;

  /** Detailed information explaining why the badge was earned or its historical significance. */
  description: string;

  /** URL path to the vector icon or image asset representing the badge visually. */
  iconUrl: string;

  /** The amount of bonus Experience Points (XP) granted to the user upon unlocking. */
  pointsReward: number;
}

/*** A specialized UI wrapper used to manage the visual state of a badge within lists.
 * It separates the badge's static data from the user's current progress state.
 */
export interface BadgeDisplay {
  /** The core Badge data object. */
  badge: Badge;

  /** * A boolean flag used by the UI to determine rendering logic:
   * - true: Render in full color with interactive details.
   * - false: Render in grayscale or with a 'locked' overlay.
   */
  isUnlocked: boolean;
}
