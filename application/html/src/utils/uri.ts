/* API Paths */

export function apiTest() {
  return `/api/test`;
}

export function apiTracking() {
  return `/api/tracking`;
}

/* URI Paths */
export function uriHome() {
  return `/`;
}

export function uriAbout(id?: string) {
  return id ? `/about/${id}` : `/about`;
}

export function uriLogin() {
  return `/login`;
}

export function uriRegistration() {
  return `/registration`;
}

export function uriForget() {
  return `/forget`;
}

export function uriSearch() {
  return `/search`;
}

export function uriDashboard() {
  return `/dashboard`;
}

export function uriUpload() {
  return `/upload`;
}

export function uriTracking(id?: string | number) {
  return id !== undefined ? `/tracking/${id}` : `/tracking`;
}

export function uriApiTest() {
  return `/api_test`;
}