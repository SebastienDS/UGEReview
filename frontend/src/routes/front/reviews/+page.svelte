<script>
    export let data;

    import { goto } from '$app/navigation';
    import { authToken } from '$lib/auth';
    import NavBar from '$lib/components/NavBar.svelte';
    import Notification from '$lib/components/Notification.svelte';
    import { formatDate } from '$lib/utils';


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
            console.log(error)
        }
    }

    async function markNotificationAsRead(e) {
        const notificationId = e.detail.notificationId
        data.notifications = data.notifications.filter(n => n.id != notificationId)
    }

    async function redirectToNotification(e) {
        goto(e.detail.link)
    }
</script>

<div class="container">
    <NavBar/>

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
                <li class="list-group-item list-group-item-action list-group-item-info">
                    <Notification notification={notification} on:markAsRead={markNotificationAsRead} on:redirect={redirectToNotification}/>
                </li>
            {/each}
        </ul>
    {/if}


    <ul class="list-group mb-3">
        {#each data.reviews as review}
            <li class="list-group-item position-relative">
                <div class="row">
                    <a href="/front/reviews/{review.id}" class="col-9">{review.title}</a>
                    <div class="col-3" style="text-align:end;">
                        <a href="/front/users/{review.author.id}">{review.author.username}</a>
                        <p>{formatDate(new Date(review.date))}</p>
                    </div>
                </div>
            </li>
        {/each}
    </ul>
</div>