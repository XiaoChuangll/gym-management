// 文档加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    // 初始化工具提示
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl)
    });
    
    // 初始化弹出框
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl)
    });
    
    // 表格搜索功能
    document.getElementById('searchInput')?.addEventListener('keyup', function() {
        var input, filter, table, tr, td, i, txtValue;
        input = this;
        filter = input.value.toUpperCase();
        table = document.querySelector('.table');
        tr = table?.getElementsByTagName('tr');
        
        if (!tr) return;
        
        for (i = 1; i < tr.length; i++) {
            var found = false;
            var tds = tr[i].getElementsByTagName('td');
            for (var j = 0; j < tds.length; j++) {
                td = tds[j];
                if (td) {
                    txtValue = td.textContent || td.innerText;
                    if (txtValue.toUpperCase().indexOf(filter) > -1) {
                        found = true;
                        break;
                    }
                }
            }
            tr[i].style.display = found ? '' : 'none';
        }
    });
    
    // 表单验证
    var forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
    
    // 加载用户资料
    loadUserProfile();
    
    // 移动端菜单按钮点击事件
    const mobileMenuButton = document.getElementById('mobile-menu-button');
    const mobileMenu = document.getElementById('mobile-menu');
    if (mobileMenuButton && mobileMenu) {
        mobileMenuButton.addEventListener('click', function() {
            mobileMenu.classList.toggle('h-0');
            mobileMenu.classList.toggle('h-auto');
        });
    }
});

// 确认删除
function confirmDelete(url, name) {
    if (confirm('确定要删除 ' + name + ' 吗？此操作无法撤销。')) {
        window.location.href = url;
    }
}

// 加载用户资料函数
function loadUserProfile() {
    // 如果页面中不存在用户头像或用户名元素，则不处理
    if (!document.getElementById('headerUserAvatar') && 
        !document.getElementById('navbarUserAvatar') && 
        !document.getElementById('headerDisplayName') && 
        !document.getElementById('navbarDisplayName')) {
        return;
    }
    
    // 获取用户资料
    fetch('/api/user-profile')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // 更新导航栏用户名
                updateElementText('navbarDisplayName', data.displayName || data.username);
                updateElementText('headerDisplayName', data.displayName || data.username);
                
                // 更新导航栏头像
                updateElementSrc('navbarUserAvatar', getAvatarUrl(data));
                updateElementSrc('headerUserAvatar', getAvatarUrl(data));
            }
        })
        .catch(error => {
            console.error('获取用户资料失败:', error);
        });
}

// 更新元素文本
function updateElementText(elementId, text) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = text;
    }
}

// 更新元素src属性
function updateElementSrc(elementId, src) {
    const element = document.getElementById(elementId);
    if (element) {
        element.src = src;
    }
}

// 获取头像URL
function getAvatarUrl(userData) {
    if (userData.avatarUrl) {
        return userData.avatarUrl;
    } else {
        // 使用默认头像
        return 'https://www.gravatar.com/avatar/' + 
                md5(userData.email || userData.username) + 
                '?d=identicon&s=200';
    }
}

// 简化版MD5函数，用于Gravatar头像
function md5(string) {
    if(!string) return '00000000000000000000000000000000';
    
    // 简化版，生成伪随机值
    let hash = '';
    const seed = string.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
    const random = new Math.seedrandom(seed.toString());
    
    for (let i = 0; i < 32; i++) {
        hash += Math.floor(random() * 16).toString(16);
    }
    return hash;
}

// 伪随机数生成器（用于确定性MD5替代方案）
Math.seedrandom = function(seed) {
    let s = parseInt(seed, 36) || 0;
    return function() {
        s = (s * 9301 + 49297) % 233280;
        return s / 233280;
    };
}; 