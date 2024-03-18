async function fetchReviews(userId) {
    try {
        const response = await fetch("/api/v1/users/" + userId + "/responses" );
        if (response.status !== 200) {
            goto("/front/error/" + response.status)
            return;
        }
        return response.json();
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