<script xmlns:on="http://www.w3.org/1999/xhtml">
    export let comment;
    export let responses;
    export let reviewId;

  let isVisible = false;
  let textareaValue = "";

  function answer() {
    isVisible = true;
  }

  function sendResponse() {

      var object = {
          "id": comment.id,
          "content": textareaValue
      };
      const url = "/api/v1/reviews/" + reviewId + "/response";
      fetch(url, {
          method: 'POST',
          headers: {
              'Content-Type': 'application/json'
          },
          body: JSON.stringify(object)
      })
      .then(response => {
          if(response.ok){
              isVisible = false;
              textareaValue = "";
              const dataPromise = response.json();
              dataPromise.then(dataResponse => {
                  responses = [...responses, dataResponse]
              })

          } else {
              console.log(response);
          }
      })
      .catch(error => {
          console.error('Erreur lors de la requête PUT :', error);
      });
  }
</script>
<div class="col-12" >
    {#if !isVisible}
      <button style="width: inherit;" on:click={answer}>
          Répondre
      </button>
    {/if}
    {#if isVisible}
      <div>
          <textarea style="width: inherit;" bind:value={$textareaValue}></textarea>
          <button on:click={sendResponse}>
              Envoyer
          </button>
      </div>
    {/if}
</div>
