<script>
  import { authToken } from '$lib/auth';
  import { userData } from '$lib/userData';
  
  
  async function deleteProfile() {
    try {
      const response = await fetch("/deleteProfile", {
        method: 'POST',
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
  
  deleteProfile()
</script>
  
Votre compte a bien été supprimé, retournez sur la home page

<a href="/front">Home</a>
    
    
    