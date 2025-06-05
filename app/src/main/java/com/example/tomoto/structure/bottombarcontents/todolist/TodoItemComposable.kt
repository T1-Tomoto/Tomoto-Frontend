package com.example.pomato.UIcomponents

/*
@Composable
fun ToDoListItemComposable2(
    item: ToDoItem,
    onToggleComplete: (ToDoItem) -> Unit,
    onDeleteItem: (ToDoItem) -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isCompleted,
            onCheckedChange = { onToggleComplete(item.copy(isCompleted = it)) },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary, // 이미지의 보라색 느낌
                uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.text,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = if (item.isCompleted) FontWeight.Light else FontWeight.Normal,
                    color = if (item.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
            )
            if (item.dueDate.isNotBlank()) {
                Text(
                    text = item.dueDate,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = { onDeleteItem(item) }) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete Task",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}*/