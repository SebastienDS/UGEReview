import { authToken } from '$lib/auth';


async function fetchReviews() {
    try {
        const response = await fetch("/api/v1/reviews", {
            headers: {
                'Authorization': authToken.get()
            }
        });
        return response.json();
    } catch (error) {
        console.log(error);
        return { error: error }
    }
}

export async function load({ params }) {
    return {
        reviews: await fetchReviews()
    };
}