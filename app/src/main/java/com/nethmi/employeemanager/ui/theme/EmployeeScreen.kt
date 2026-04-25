package com.nethmi.employeemanager.ui.theme

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.nethmi.employeemanager.data.Employee
import com.nethmi.employeemanager.viewmodel.EmployeeViewModel
import kotlinx.coroutines.launch

// ── Department colour system ──────────────────────────────────────────────────
private data class DeptTheme(
    val accent: Color,
    val surface: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private fun deptTheme(dept: String): DeptTheme = when (dept) {
    "Data"         -> DeptTheme(Color(0xFF1A73E8), Color(0xFFE8F0FE), Icons.Outlined.Storage)
    "Web"          -> DeptTheme(Color(0xFF0F9D58), Color(0xFFE6F4EA), Icons.Outlined.Language)
    "Intranet"     -> DeptTheme(Color(0xFFF29900), Color(0xFFFEF7E0), Icons.Outlined.Router)
    "Assurance"    -> DeptTheme(Color(0xFFE53935), Color(0xFFFCE8E6), Icons.Outlined.VerifiedUser)
    "Applications" -> DeptTheme(Color(0xFF9334E6), Color(0xFFF3E8FD), Icons.Outlined.Apps)
    else           -> DeptTheme(Color(0xFF5F6368), Color(0xFFF1F3F4), Icons.Outlined.Business)
}

// ── Root Screen ───────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeScreen(
    viewModel: EmployeeViewModel,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val loggedInUser = sharedPref.getString("CURRENT_USER", "User") ?: "User"

    val filteredEmployees = viewModel.filteredEmployees.value
    val isLoading         = viewModel.isLoading.value
    val searchQuery       = viewModel.searchQuery.value
    val selectedDept      = viewModel.selectedDepartment.value

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

    var showDeleteDialog  by remember { mutableStateOf<Employee?>(null) }
    var showUpdateDialog  by remember { mutableStateOf<Employee?>(null) }
    var showDetailsDialog by remember { mutableStateOf<Employee?>(null) }
    var showAddDialog     by remember { mutableStateOf(false) }
    var showFilterMenu    by remember { mutableStateOf(false) }
    var showReportDialog  by remember { mutableStateOf(false) }

    val departments   = listOf("Data", "Web", "Intranet", "Assurance", "Applications")
    val filterOptions = listOf("All") + departments

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier             = Modifier.width(300.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.80f))))
                        .padding(horizontal = 28.dp, vertical = 44.dp)
                ) {
                    Column {
                        Box(contentAlignment = Alignment.Center) {
                            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.20f), modifier = Modifier.size(84.dp)) {}
                            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.90f), modifier = Modifier.size(72.dp)) {
                                Icon(Icons.Default.Person, null, modifier = Modifier.padding(14.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        Text("Welcome back,", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.75f), letterSpacing = 0.4.sp)
                        Text(loggedInUser, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp), color = Color.White)
                        Spacer(Modifier.height(14.dp))
                        Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.20f)) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.AdminPanelSettings, null, tint = Color.White, modifier = Modifier.size(13.dp))
                                Text("Administrator", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                ListItem(
                    headlineContent = { Text(if (isDarkMode) "Dark Mode" else "Light Mode", fontWeight = FontWeight.Medium) },
                    leadingContent = { Icon(if (isDarkMode) Icons.Default.DarkMode else Icons.Outlined.WbSunny, null, modifier = Modifier.size(20.dp)) },
                    trailingContent = { Switch(checked = isDarkMode, onCheckedChange = onThemeChange) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))

                NavigationDrawerItem(
                    label = { Text("Dashboard", fontWeight = FontWeight.SemiBold) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.Dashboard, null) },
                    badge = { Text(filteredEmployees.size.toString(), modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // ── REPORTS BUTTON ──
                NavigationDrawerItem(
                    label = { Text("Reports", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; showReportDialog = true },
                    icon = { Icon(Icons.Outlined.Assessment, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Departments", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Outlined.AccountTree, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.weight(1f))
                NavigationDrawerItem(
                    label = { Text("Sign Out", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; onLogout() },
                    icon = { Icon(Icons.Default.Logout, null, tint = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.height(28.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TEAM MANAGER", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 3.sp))
                            Text("${filteredEmployees.size} members", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.MenuOpen, "Menu") } }
                )
            },
            floatingActionButton = {
                LargeFloatingActionButton(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(28.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp).shadow(elevation = 18.dp, shape = RoundedCornerShape(28.dp), ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(28.dp))
                        Text("ADD", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold))
                    }
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search name or role…", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant, focusedBorderColor = MaterialTheme.colorScheme.primary)
                    )
                    Surface(onClick = { showFilterMenu = true }, shape = RoundedCornerShape(18.dp), color = if (selectedDept == "All") MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(56.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Tune, null) }
                        DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                            filterOptions.forEach { dept ->
                                DropdownMenuItem(text = { Text(dept) }, onClick = { viewModel.onDepartmentSelected(dept); showFilterMenu = false })
                            }
                        }
                    }
                }

                if (selectedDept != "All") {
                    val t = deptTheme(selectedDept)
                    Surface(modifier = Modifier.padding(start = 20.dp, bottom = 8.dp), shape = RoundedCornerShape(20.dp), color = t.surface) {
                        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(t.icon, null, tint = t.accent, modifier = Modifier.size(14.dp))
                            Text(selectedDept, style = MaterialTheme.typography.labelMedium, color = t.accent, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { viewModel.onDepartmentSelected("All") }, modifier = Modifier.size(16.dp)) { Icon(Icons.Default.Close, null, modifier = Modifier.size(13.dp)) }
                        }
                    }
                }

                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(filteredEmployees) { employee ->
                            EmployeeItem(employee, { showDeleteDialog = employee }, { showUpdateDialog = employee }, { showDetailsDialog = employee })
                        }
                    }
                }
            }

            // ── DETAILS DIALOG  ──
            showDetailsDialog?.let { employee ->
                EmployeeDetailsDialog(employee = employee, onDismiss = { showDetailsDialog = null })
            }

            // ── REPORT DIALOG ──
            if (showReportDialog) {
                EmployeeReportDialog(employees = filteredEmployees, onDismiss = { showReportDialog = false })
            }

            showDeleteDialog?.let { employee ->
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = null },
                    shape = RoundedCornerShape(28.dp),
                    title = { Text("Remove Member?") },
                    text = { Text("Are you sure you want to remove ${employee.name}?") },
                    confirmButton = {
                        Button(onClick = { viewModel.deleteEmployee(employee.id); Toast.makeText(context, "Employee Removed", Toast.LENGTH_SHORT).show(); showDeleteDialog = null }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Remove") }
                    },
                    dismissButton = { OutlinedButton(onClick = { showDeleteDialog = null }) { Text("Keep") } }
                )
            }

            showUpdateDialog?.let { employee ->
                UpdateEmployeeDialog(employee, departments, { showUpdateDialog = null }, { updatedEmp ->
                    viewModel.updateEmployee(employee.id, updatedEmp)
                    Toast.makeText(context, "Profile Updated ✓", Toast.LENGTH_SHORT).show()
                    showUpdateDialog = null
                })
            }

            if (showAddDialog) {
                AddEmployeeDialog(departments, { showAddDialog = false }, { newEmp ->
                    viewModel.addEmployee(newEmp)
                    Toast.makeText(context, "Member Added ✓", Toast.LENGTH_SHORT).show()
                    showAddDialog = false
                })
            }
        }
    }
}

// ── EMPLOYEE DETAILS DIALOG (Pictured exactly) ──
@Composable
fun EmployeeDetailsDialog(employee: Employee, onDismiss: () -> Unit) {
    val t = deptTheme(employee.department)
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxWidth(0.92f).wrapContentHeight(), shape = RoundedCornerShape(32.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 6.dp) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(Brush.linearGradient(listOf(t.accent.copy(alpha = 0.85f), t.accent.copy(alpha = 0.40f))), shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)), contentAlignment = Alignment.Center) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.30f), modifier = Modifier.size(104.dp)) {}
                        Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(90.dp)) {
                            AsyncImage(model = employee.avatar, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                        }
                    }
                    Box(modifier = Modifier.align(Alignment.Center).offset(x = 34.dp, y = 34.dp)) {
                        Surface(shape = CircleShape, color = t.surface, modifier = Modifier.size(28.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(t.icon, null, tint = t.accent, modifier = Modifier.size(15.dp)) }
                        }
                    }
                }
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(employee.name, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp))
                    Text(employee.designation, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(20.dp), color = t.surface) {
                        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(t.icon, null, tint = t.accent, modifier = Modifier.size(14.dp))
                            Text(employee.department, style = MaterialTheme.typography.labelMedium, color = t.accent, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(20.dp))
                    ProfileDetailRow(Icons.Outlined.Badge, "Employee ID", employee.id.take(8))
                    Spacer(Modifier.height(12.dp))
                    ProfileDetailRow(Icons.Outlined.AccountTree, "Department", employee.department, t.accent)
                    Spacer(Modifier.height(12.dp))
                    ProfileDetailRow(Icons.Outlined.Work, "Designation", employee.designation)
                    Spacer(Modifier.height(28.dp))
                    Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = t.accent)) {
                        Text("Close Profile", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, valueColor: Color = Color.Unspecified) {
    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(18.dp)) }
            }
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = if (valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor)
            }
        }
    }
}

// ── EMPLOYEE REPORT DIALOG ──
@Composable
fun EmployeeReportDialog(employees: List<Employee>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize().padding(16.dp), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Staff Report", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text("Total Members: ${employees.size}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(employees) { emp ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(emp.name, fontWeight = FontWeight.Bold)
                                Text(emp.department, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(emp.designation, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                        }
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().padding(top = 16.dp), shape = RoundedCornerShape(12.dp)) { Text("Close Report") }
            }
        }
    }
}

// ── Employee List Item ──
@Composable
fun EmployeeItem(employee: Employee, onDeleteClick: () -> Unit, onEditClick: () -> Unit, onItemClick: () -> Unit) {
    val t = deptTheme(employee.department)
    Card(modifier = Modifier.fillMaxWidth().clickable { onItemClick() }, shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(shape = RoundedCornerShape(20.dp), color = t.surface, modifier = Modifier.size(72.dp)) {
                AsyncImage(model = employee.avatar, contentDescription = null, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)), contentScale = ContentScale.Crop)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(employee.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(employee.designation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                Surface(shape = RoundedCornerShape(8.dp), color = t.surface, modifier = Modifier.padding(top = 4.dp)) {
                    Text(employee.department, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = t.accent, fontWeight = FontWeight.Bold)
                }
            }
            Column {
                IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = onDeleteClick) { Icon(Icons.Default.DeleteOutline, null, tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

// ── ADD DIALOG (Pictured exactly) ──
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeDialog(departments: List<String>, onDismiss: () -> Unit, onAdd: (Employee) -> Unit) {
    var name by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var department by remember { mutableStateOf(departments[0]) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        icon = { Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(52.dp)) { Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.PersonAdd, null, tint = MaterialTheme.colorScheme.onPrimaryContainer) } } },
        title = { Text("New Team Member", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, leadingIcon = { Icon(Icons.Outlined.Badge, null) }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = designation, onValueChange = { designation = it }, label = { Text("Designation") }, leadingIcon = { Icon(Icons.Outlined.Work, null) }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(value = department, onValueChange = {}, readOnly = true, label = { Text("Department") }, leadingIcon = { Icon(Icons.Outlined.AccountTree, null) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(14.dp))
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        departments.forEach { dept -> DropdownMenuItem(text = { Text(dept) }, onClick = { department = dept; expanded = false }) }
                    }
                }
                Text("Name and designation are required", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f))
            }
        },
        confirmButton = { Button(onClick = { if(name.isNotEmpty()) onAdd(Employee("", name, designation, department, "https://i.pravatar.cc/150?u=$name")) }, enabled = name.isNotEmpty(), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) { Text("Create Profile", fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cancel") } }
    )
}

// ── UPDATE DIALOG ──
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateEmployeeDialog(employee: Employee, departments: List<String>, onDismiss: () -> Unit, onUpdate: (Employee) -> Unit) {
    var name by remember { mutableStateOf(employee.name) }
    var designation by remember { mutableStateOf(employee.designation) }
    var department by remember { mutableStateOf(employee.department) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, shape = RoundedCornerShape(14.dp))
                OutlinedTextField(value = designation, onValueChange = { designation = it }, label = { Text("Job Title") }, shape = RoundedCornerShape(14.dp))
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(value = department, onValueChange = {}, readOnly = true, label = { Text("Dept") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor(), shape = RoundedCornerShape(14.dp))
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        departments.forEach { dept -> DropdownMenuItem(text = { Text(dept) }, onClick = { department = dept; expanded = false }) }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onUpdate(employee.copy(name = name, designation = designation, department = department)) }, shape = RoundedCornerShape(14.dp)) { Text("Save Changes") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Discard") } }
    )
}