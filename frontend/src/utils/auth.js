export function getHomePath(role) {
  switch (role) {
    case 'ADMIN':
    case 'OWNER':
    case 'KITCHEN':
    case 'CUSTOMER':
      return '/app/restaurants';
    default:
      return '/app';
  }
}

export function getRoleLabel(role) {
  if (!role) return 'Guest';
  return role.charAt(0) + role.slice(1).toLowerCase();
}

export function canAccessRoute(role, allowedRoles = []) {
  if (!allowedRoles.length) return true;
  if (!role) return false;
  return allowedRoles.includes(role);
}
