<script>
    import { goto } from '$app/navigation';
    import { authToken } from '$lib/auth';
    import Notification from '$lib/components/Notification.svelte';

    const isAuthenticated = authToken.get() != null;

    let notifications = [];

        
    async function fetchNotifications() {
        try {
            const response = await fetch("/api/v1/notifications", {
                headers: {
                    'Authorization': authToken.get()
                }
            });
            notifications = await response.json();
        } catch (error) {
            console.log(error);
        }
    }

    async function fetchNotificationsIfAuthenticated() {
        if (authToken.get() === null) return;
        fetchNotifications()
    }

    async function markNotificationAsRead(e) {
        const notificationId = e.detail.notificationId
        notifications = notifications.filter(n => n.id != notificationId)
    }

    async function redirectToNotification(e) {
        goto(e.detail.link)
    }

    fetchNotificationsIfAuthenticated()
</script>

<div class="d-flex justify-content-between align-items-center my-3">
    <nav>
        <a href="/front">Home</a>
        <a href="/front/reviews">Reviews</a>
        <a href="/front/createReview">CreateReview</a>
    </nav>

    <div class="d-flex">
        {#if isAuthenticated}
            <div class="dropdown mx-3">
                <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownNotifications" data-bs-toggle="dropdown" data-bs-auto-close="outside"  aria-expanded="false">
                    Notifications {#if notifications.length > 0}
                        <span>({notifications.length})</span>
                    {/if}
                </button>

                <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="dropdownNotifications" style="min-height: 100px;">
                    {#each notifications as notification}
                        <li class="dropdown-item d-flex align-items-center">
                            <Notification notification={notification} on:markAsRead={markNotificationAsRead} on:redirect={redirectToNotification}/>
                        </li>   
                    {/each}
                </ul>
            </div>
        {/if}

        {#if isAuthenticated}
            <a href="/front/logout">Logout</a>
        {:else}
            <a href="/front/login">Login</a>
        {/if}
    </div>
</div>