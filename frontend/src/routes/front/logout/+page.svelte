<script>
import { authToken } from '$lib/auth';

async function logout() {
  try {
    const response = await fetch("/logout", {
      headers: {
        'Authorization': $authToken
      }
    });
    if (!response.ok) return

    authToken.clear();
    console.log("token", $authToken)
  } catch (error) {
    console.error(error);
  }
}

logout()
</script>

Vous avez été déconnnecté, retournez sur la home page

<a href="/front">Home</a>


