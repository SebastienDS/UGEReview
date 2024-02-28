import { authToken } from '$lib/auth';
import { requireAuth } from '$lib/requireAuth';


async function fetchData() {
  return { reviews: [] }
}

export async function load(params) {
    if (!requireAuth()) return;
    return await fetchData();
}