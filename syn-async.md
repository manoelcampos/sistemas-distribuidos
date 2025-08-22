Olá pessoal. Gostaria de compartilhar uma resposta de um estudante para uma questão da prova, que vale muito como estudo. Segue abaixo.

# Questão: 
    Descreva: o que é a métrica de disponibilidade, de tolerância a falhas e como uma afeta a outra.

# Resposta: 
    - (a) Comunicação síncrona ocorre de forma simultânea (ex: telefonemas e calls) e (b) rápida. 
    - (c) Comunicaçao assíncrona é mais lenta (ex: email).

# Comentários do Professor:

(a) Telefonemas e calls são realmente síncronas pois as pessoas precisam estar lá no mesmo momento, uma esperando pela outra falar. 

Telefonemas, calls (no Google Meet por exemplo) e email são tipo de comunicação feitas em geral para que pessoas possam se comunicar. 
Obviamente os sistemas que permitem isso funcionar são distribuídos, mas eles são usados primariamente pra comunicação humana 
(posso ter sistemas enviando emails para outros sistemas lerem).

Então, nem todos os detalhes dos exemplos dados na resposta necessariamente se aplicam quando falamos de comunicação sync e async entre dois sistemas (no lugar de duas pessoas). Por isso, precisamos distinguir alguns pontos de "comunicação humana" versus "comunicação entre sistemas distribuídos".

(b) Na comunicação humana, comunicação síncrona é realmente mais rápida. Em uma call você já pode falar o que precisa, fazer todas as perguntas e receber todas as respostas num intervalo de tempo definido, sem precisar mandar uma pergunta e aguardar horas ou dias por uma resposta (como no caso de email).
Então síncrono em qualquer caso (comunicação humana ou entre sistemas) significa que você manda uma pergunta (requisição) e fica aguardando pela resposta.

Mas não necessariamente quer dizer que a resposta vai chegar mais rápido. No exemplo de comunicação humana numa call (que é síncrona), a resposta vai vir mais rápido que aguardar um email. Mas em comunicação entre 2 sistemas, síncrono não significa necessariamente que a resposta vem mais rápido.

Da mesma forma que um telefone ou call é sync, eu posso usar uma função para enviar um email de forma sync também. Só que neste caso, não significa que o que enviou o email vai ficar esperando a pessoa que recebeu responder, apenas significa que eu vou ficar aguardando uma resposta do servidor de email indicando se a mensagem foi de fato enviada. 

Eu mandei o email de forma sync e não tenho uma "resposta da pessoa destinarária" mais rapidamente.
Portanto, sync não necessariamente significa "mais rápido". Depende do contexto.
Adicionalmente, eu também posso mandar o email de form async, usando uma função eu faça isso assíncronamente.
Neste caso, a única coisa que muda é que não ficarei esperando uma resposta do servidor pra indicar se o email foi enviado ou não.
Então, no contexto de comunicação humana, email é async, mas no contexto de comunicação de sistemas, depende do tipo de função que você usou pra enviar o email.

(c) A comunicação humana por email é assíncrona (pois vc não precisa ficar aguardando o email, ele vai chegar no servidor que entregará na sua caixa de entrada). Mas não quer dizer necessariamente que comunicação assíncrona entre sistemas distribuídos (no lugar de entre pessoas) é mais lenta.
Na verdade, entre sistemas é geralmente mais rápida, mas tudo depende. 

Se você precisa enviar uma mensagem cujo tamanho total (incluindo cabeçalho) é o mesmo, tanto numa chamada síncrona quanto assíncrona, então o tempo de resposta é o mesmo. Mas alguns tipos de chamadas assíncronas vão levar menos tempos pois enviamos menos dados (é o caso quando usamos o protocolo WebSocket). 

Portanto, comunicação humana (mesmo que usando sistemas) em alguns pontos é diferente de comunicação entre sistemas.

E no final, as chamadas async amenizam problemas de escalabilidade justamente porque o usuário pode não perceber o atraso, pois tá fazendo outra coisa no app enquanto a resposta não chega.
