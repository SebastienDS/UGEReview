async function fetchReviews(userId) {
    try {
        const reviews = await fetch("/api/v1/users/" + userId + "/reviews" );
        return reviews.json();
    } catch (error) {
        console.log(error);
        return { error: error }
    }
}

export async function load({ params }) {
    const [reviews] = await Promise.all([
		fetchReviews(params.userId)
	]);
    return {
        reviews
    };
}