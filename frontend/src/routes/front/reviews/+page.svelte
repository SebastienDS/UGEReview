<script>
    export let data;

    import { authToken } from '$lib/auth';

    const isAuthenticated = authToken.get() != null;

    let search = '';

    async function searchReviews() {
        try {
            const response = await fetch("/api/v1/reviews?search=" + search, {
                headers: {
                    'Authorization': authToken.get()
                }
            });
            var reviews = await response.json();
            data.reviews = reviews
        } catch (error) {
            return { error: error }
        }
    }
</script>

<div class="container">
    <nav>
        <a href="/front">Home</a>
        <a href="/front/test">Test</a>
        <a href="/front/reviews">Reviews</a>
        <a href="/front/login">Login</a>
        <a href="/front/logout">Logout</a>
    </nav>

    <h1>UGERevue</h1>

    {#if isAuthenticated}
        <p>Connected</p>
    {/if}

    <form on:submit|preventDefault={searchReviews}>
        <div class="input-group mb-3">
            <input type="text" class="form-control" placeholder="Search" bind:value={search}>
            <div class="input-group-append">
                <button class="btn btn-primary" type="submit">Submit</button>
            </div>
        </div>
    </form>

    {#if isAuthenticated}
        <ul class="list-group mb-3">
            {#each data.notifications as notification}
                <li class="list-group-item list-group-item-action list-group-item-info d-flex align-items-center">
                    <!-- <form th:action="@{'/notifications/' + ${notification.getId()} + '/markAsRead'}" method="post" class="me-3">
                        <button type="submit" class="btn-close" aria-label="Close"></button>
                    </form>
                    <form th:action="@{'/notifications/' + ${notification.getId()} + '/markAsRead/redirect'}" method="post" class="position-relative">
                        <button type="submit" class="stretched-link btn btn-link" th:text="${notification}"/>
                    </form> -->
                    <div class="me-3">
                        <button class="btn-close"></button>
                    </div>
                    <div class="position-relative">
                        <button type="submit" class="stretched-link btn btn-link">
                            {JSON.stringify(notification)}
                        </button>
                    </div>
                </li>
            {/each}
        </ul>
    {/if}


    <ul class="list-group mb-3">
        {#each data.reviews as review}
            <li class="list-group-item position-relative">
                <a href="/front/reviews/{review.id}" class="stretched-link">
                    {JSON.stringify(review)}
                </a>
            </li>
        {/each}
    </ul>
</div>