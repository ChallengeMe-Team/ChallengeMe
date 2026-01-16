/**
 *These structures define the core business logic for quests.
 * They ensure that every challenge has a standardized difficulty tier,
 * a consistent point valuation, and proper authorship metadata.
 */

/**
 *  A type-safe enumeration that restricts quest complexity to three specific tiers.
 * Used by the UI for color-coding and by the Backend for XP validation.
 */
export enum Difficulty {
  EASY = 'EASY',     // Low complexity, quick completion.
  MEDIUM = 'MEDIUM', // Moderate effort required.
  HARD = 'HARD'      // High complexity, long-term commitment.
}

/**
 * Represents the structural blueprint of a quest. This interface is utilized
 * across the Catalog, Dashboard, and Creation forms.
 */
export interface Challenge {
  /** Unique identifier (UUID) for database indexing and relational linking. */
  id: string;

  /** The headline of the quest (e.g., '30 Day Pushup Challenge'). */
  title: string;

  /** A detailed explanation of the quest goals and requirements. */
  description: string;

  /** The thematic group the quest belongs to (e.g., 'Fitness', 'Coding'). */
  category: string;

  /** The complexity tier as defined by the Difficulty Enum. */
  difficulty: Difficulty;

  /** The amount of Experience Points (XP) a user earns upon completion. */
  points: number;

  /** The username of the user who originally designed and published the quest. */
  createdBy: string;
}
