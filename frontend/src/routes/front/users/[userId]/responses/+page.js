async function fetchReviews(userId) {
    try {
        const responses = await fetch("/api/v1/users/" + userId + "/responses" );
        return responses.json();
    } catch (error) {
        console.log(error);
        return { error: error }
    }
}

export async function load({ params }) {
    const [responses] = await Promise.all([
		fetchReviews(params.userId)
	]);
    return {
        responses,
        userId: params.userId
    };
}