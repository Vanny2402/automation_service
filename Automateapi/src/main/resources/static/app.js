fetch('/api/testcases')
  .then(response => response.json())
  .then(data => {
    const tableBody = document.getElementById('test-case-list');
    tableBody.innerHTML = '';  

    data.forEach(testCase => {
      const row = document.createElement('tr');
      row.innerHTML = `
        <td>${testCase.name}</td>
        <td>${testCase.project}</td>
        <td>${testCase.owner}</td>
        <td>${testCase.status}</td>
        <td>
          <button>Edit</button>
          <button>Delete</button>
          <button>Execute</button>
        </td>
      `;
      tableBody.appendChild(row);
    });
  })
  .catch(error => {
    console.error('Error fetching test cases:', error);
  });

function toggleDeviceList() {
  const deviceList = document.getElementById('device-list');
  if (deviceList.style.display === 'none') {
    deviceList.style.display = 'block';
  } else {
    deviceList.style.display = 'none';
  }
}

