import { requireAuth } from '$lib/requireAuth';

export async function load({ params }) {
    requireAuth();
}