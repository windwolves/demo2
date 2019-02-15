# demo2
in this demo, it will send message via textLocal or twilio APIs.
The sample payload looks like this:
  {
    "message" : "Test",
    "targetNumbers" :[1,2],
    "sender" : "LD"
  }
And please set the content-type to application/json.
in the payload, the message is what you want to send. The targetNumbers are the list of the numbers you want to send message to, you can set only one number in the list if you only want to send message to one. The sender is optional and it will be shown in the message if the message are sent via textLocal.

The message sending logic behind is that:
At first, we try send the message via textLocal and get the response from textLocal, if there still some targets are failed to be sent, then will try using the twilio to send the message to the remaining targets.
