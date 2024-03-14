<script>
import { authToken } from '$lib/auth';
import { goto } from '$app/navigation';
import { userData } from '$lib/userData';


const isAuthenticated = authToken.get() != null;

function goToProfile(isAuthenticated) {
    if (isAuthenticated) {
        goto(`/front/users/${userData.get().id}`);
    } else {
        goto(`/front/login`);
    }
}

goToProfile(isAuthenticated);
</script>

