#foreach($trigger in $triggers)
let elements = querySelectorAll("$trigger.getQuerySelector()");
for (let i = 0; i < elements.length; i++) {
  const data = {
    unitId: $trigger.getUnitId(),
    containerId: $trigger.getContainerId(),
    ASID: $trigger.getASID(),
    triggerId: $trigger.getTriggerId(),
    event: $trigger.getEvent(),
  }
  elements[i].addEventListener($trigger.getEvent(), async () => {
    const response = await fetch(localhost:8080/api/trigger, {
      method: 'POST',
      mode: 'cors',
      cache: 'no-cache',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({data})
    });
  });
};
#end