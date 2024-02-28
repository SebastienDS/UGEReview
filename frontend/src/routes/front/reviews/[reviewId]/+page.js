import { authToken } from '$lib/auth';


async function fetchData(reviewId) {
  try {
    const response = await fetch("/api/v1/reviews/" + reviewId, {
      headers: {
        'Authorization': authToken.get()
      }
    });
    return response.json();
  } catch (error) {
    return { error: error }
  }
}

export async function load({params}) {
    return {
        reviewId:params.reviewId,
        review: await fetchData(params.reviewId)
    };
}