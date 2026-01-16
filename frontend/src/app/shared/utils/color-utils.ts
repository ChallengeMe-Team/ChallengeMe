/**
 * A utility module that implements a deterministic procedural generation
 * engine for UI gradients.
 * * * Key Technical Aspects:
 * - Deterministic Hashing: Uses the DJB2 algorithm to ensure that a category
 * string (e.g., 'Fitness') always yields the same visual style.
 * - Linear Gradient Mapping: Generates dynamic CSS 'background' properties
 * based on a curated high-contrast palette.
 * - Dynamic Angles: Calculates varied light directions to add visual depth
 * and variety to the challenge catalog.
 */

/**
 * A curated collection of 16 high-impact color hex pairs.
 * Designed to maintain brand vibrancy in dark mode environments.
 */
export const CATEGORY_COLOR_PAIRS = [
  ['#FF512F', '#DD2476'], // Crimson -> Pink
  ['#4776E6', '#8E54E9'], // Royal Blue -> Purple
  ['#00B09B', '#96C93D'], // Teal -> Green
  ['#f38a2c', '#dbb02f'], // Orange -> Yellow
  ['#833ab4', '#fd1d1d'], // Deep Purple -> Bright Red
  ['#2193b0', '#6dd5ed'], // Blue Lagoon -> Sky
  ['#18332c', '#52bc8a'], // Forest -> Mint
  ['#e94e77', '#d68189'], // Rose -> Sand
  ['#000428', '#004e92'], // Midnight -> Deep Sea
  ['#7b4397', '#dc2430'], // Violet -> Red
  ['#157363', '#28a890'],
  ['#EB3349', '#F45C43'], // Cherry -> Sun
  ['#4b6cb7', '#182848'], // Steel -> Navy
  ['#00c6ff', '#0072ff'], // Bright Blue -> Electric
  ['#f27121', '#e94057'], // Sunset -> Fuchsia
  ['#8e44ad', '#c0392b']  // Amethyst -> Pomegranate
];

/**
 * The primary engine for procedural UI generation.
 * * Logic Flow:
 * 1. Sanitization: Returns a neutral Gray-600 if no category is provided.
 * 2. Hashing (DJB2): Transforms the string into a unique integer hash.
 * 3. Indexing: Uses modulus logic to map the hash to one of the 16 color pairs.
 * 4. Geometry: Calculates a dynamic angle (90°, 135°, 180°, or 225°) for the gradient.
 * *
 * @param category The name of the category (e.g., 'Coding', 'Mindfulness').
 * @returns A CSS style object for [ngStyle] binding.
 */
export function getCategoryGradient(category: string) {
  if (!category) return { 'background': '#4b5563' }; // Gray-600 default

  // STEP 1: DJB2 Hashing Algorithm
  let hash = 5381;
  for (let i = 0; i < category.length; i++) {
    hash = ((hash << 5) + hash) + category.charCodeAt(i);
  }

  // STEP 2: Deterministic Mapping
  const index = Math.abs(hash * 31) % CATEGORY_COLOR_PAIRS.length;
  const colors = CATEGORY_COLOR_PAIRS[index];

  // STEP 3: Dynamic Rotation
  const angle = (Math.abs(hash) % 4) * 45 + 90;

  return {
    'background': `linear-gradient(${angle}deg, ${colors[0]}, ${colors[1]})`
  };
}
