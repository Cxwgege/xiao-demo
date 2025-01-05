/**
 * 格式化日期时间
 * @param {string | Date} time 时间
 * @param {string} format 格式
 * @returns {string}
 */
export function formatDateTime(time, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!time) return ''
  const date = new Date(time)
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
} 