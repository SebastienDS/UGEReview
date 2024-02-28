import { authToken } from '$lib/auth';
import { requireAuth } from '$lib/requireAuth';


async function fetchData() {
  try {
    const response = await fetch("/api/v1/test", {
      headers: {
        'Authorization': authToken.get()
      }
    });
    return response.json();
  } catch (error) {
    return { error: error }
  }
}

export async function load(params) {
    if (!requireAuth()) return;
    return await fetchData();
}