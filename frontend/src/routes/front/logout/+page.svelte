<script>
import { authToken } from '$lib/auth';
import { userData } from '$lib/userData';


async function logout() {
  try {
    const response = await fetch("/logout", {
      headers: {
        'Authorization': $authToken
      }
    });
    if (!response.ok) return

    authToken.clear();
    userData.clear();
    console.log("token", $authToken)
  } catch (error) {
    console.error(error);
  }
}

logout()
</script>

Vous avez été déconnnecté, retournez sur la home page

<a href="/front">Home</a>


