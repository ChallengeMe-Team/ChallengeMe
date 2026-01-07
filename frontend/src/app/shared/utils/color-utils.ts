// src/app/shared/utils/color-utils.ts

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

export function getCategoryGradient(category: string) {
  if (!category) return { 'background': '#4b5563' }; // Gray-600 default

  // Algoritm DJB2 pentru hash constant
  let hash = 5381;
  for (let i = 0; i < category.length; i++) {
    hash = ((hash << 5) + hash) + category.charCodeAt(i);
  }

  // Mapare pe paletă
  const index = Math.abs(hash * 31) % CATEGORY_COLOR_PAIRS.length;
  const colors = CATEGORY_COLOR_PAIRS[index];

  // Unghi dinamic
  const angle = (Math.abs(hash) % 4) * 45 + 90;

  return {
    'background': `linear-gradient(${angle}deg, ${colors[0]}, ${colors[1]})`
  };
}
