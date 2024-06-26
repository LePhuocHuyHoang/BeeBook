import React, { useEffect } from 'react';

import axios from 'axios';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { styled, alpha } from '@mui/material/styles';
import Box from '@mui/material/Box';
import InputBase from '@mui/material/InputBase';
import SearchIcon from '@mui/icons-material/Search';
import Pagination from '@mui/material/Pagination';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import Modal from '@mui/material/Modal';
import AddNewBook from './AddNewBook';
import UpdateBook from './UpdateBook';
import { Alert, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Snackbar } from '@mui/material';
import dayjs from 'dayjs';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Grid from '@mui/material/Grid';
import { v4 as uuidv4 } from 'uuid';

const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 800,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
};

const Search = styled('div')(({ theme }) => ({
    position: 'relative',
    borderRadius: theme.shape.borderRadius,
    backgroundColor: alpha(theme.palette.common.white, 0.15),
    '&:hover': {
        backgroundColor: alpha(theme.palette.common.white, 0.25),
    },
    marginLeft: 0,
    width: '100%',
    [theme.breakpoints.up('sm')]: {
        marginLeft: theme.spacing(1),
        width: 'auto',
    },
}));

const SearchIconWrapper = styled('div')(({ theme }) => ({
    padding: theme.spacing(0, 2),
    height: '100%',
    position: 'absolute',
    pointerEvents: 'none',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
}));

const StyledInputBase = styled(InputBase)(({ theme }) => ({
    color: 'inherit',
    width: '100%',
    '& .MuiInputBase-input': {
        border: '1px solid black',
        padding: theme.spacing(1, 1, 1, 0),
        // vertical padding + font size from searchIcon
        paddingLeft: `calc(1em + ${theme.spacing(4)})`,
        transition: theme.transitions.create('width'),
        [theme.breakpoints.up('sm')]: {
            width: '12ch',
            '&:focus': {
                width: '50ch',
            },
        },
    },
}));

export default function ManageBook() {
    const [books, setBooks] = React.useState([]);
    const [currentPage, setCurrentPage] = React.useState(1);
    const [totalPages, setTotalPages] = React.useState(1);
    const [open, setOpen] = React.useState(false);
    const [showUpdateModal, setShowUpdateModal] = React.useState(false);
    const [updatingBook, setUpdatingBook] = React.useState(null);
    const [addBook, setAddBook] = React.useState(null);
    const [deleteBookId, setDeleteBookId] = React.useState(null);
    const [minPrice, setMinPrice] = React.useState('');
    const [maxPrice, setMaxPrice] = React.useState('');
    const [typeName, setTypeName] = React.useState('');
    const [authorName, setAuthorName] = React.useState('');
    const [typeOptions, setTypeOptions] = React.useState([]);
    const [authorOptions, setAuthorOptions] = React.useState([]);
    const [pointBooks, setPointBooks] = React.useState([]);

    const [error, setError] = React.useState('');
    const [success, setSuccess] = React.useState('');
    const [searchKeyword, setSearchKeyword] = React.useState('');

    const handleOpen = (bookId) => {
        setOpen(true);
        setAddBook(bookId);
    };
    const handleClose = () => setOpen(false);

    const handleMinPriceChange = (event) => {
        setMinPrice(event.target.value);
        checkPriceRange(event.target.value, maxPrice);
    };

    const handleMaxPriceChange = (event) => {
        setMaxPrice(event.target.value);
        checkPriceRange(minPrice, event.target.value);
    };

    const handleTypeNameChange = (event) => {
        setTypeName(event.target.value);
    };

    const handleAuthorNameChange = (event) => {
        setAuthorName(event.target.value);
    };

    const checkPriceRange = (min, max) => {
        if (min && max && parseInt(min) > parseInt(max)) {
            setError('Giá tối thiểu không được lớn hơn giá tối đa');
        } else {
            setError('');
        }
    };

    const fetchBooks = async () => {
        try {
            const response = await fetch(`http://localhost:8098/admin/book/all?page=${currentPage - 1}&size=5`, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();

            const books = data.content[0].content.map((book) => ({
                ...book,
                publicationYear: dayjs(book.publicationYear).format('YYYY-MM-DD'),
                typeName: book.types[0]?.name || '',
                authorName: book.authors[0]?.name || '',
            }));

            console.log(books);
            setBooks(books);
            setTotalPages(data.content[0].totalPages);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };

    React.useEffect(() => {
        fetchBooks();
    }, [currentPage]);

    const handlePageChange = (event, pageNumber) => {
        setCurrentPage(pageNumber);
    };

    const [reloadPage, setReloadPage] = React.useState(false);

    const handleUpdate = (bookId) => {
        setUpdatingBook(bookId);
        setShowUpdateModal(true); // Khi nhấn nút "Cập nhật", hiển thị modal
    };

    const handleCloseUpdateModal = () => {
        setShowUpdateModal(false); // Hàm để đóng modal
    };

    useEffect(() => {
        const fetchTypes = async () => {
            try {
                const response = await axios.get('http://localhost:8098/admin/type/all?size=30', {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('token')}`,
                    },
                });
                const types = response.data.content[0].content;
                const typeNames = types.map((type) => ({ label: type.name }));
                setTypeOptions(typeNames);
            } catch (error) {
                console.error('Error fetching types:', error);
            }
        };

        const fetchAuthors = async () => {
            try {
                const response = await axios.get('http://localhost:8098/admin/author/all?size=30', {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('token')}`,
                    },
                });
                const authors = response.data.content[0].content;
                const authorNames = authors.map((author) => ({ label: author.name }));
                setAuthorOptions(authorNames);
            } catch (error) {
                console.error('Error fetching authors:', error);
            }
        };

        fetchTypes();
        fetchAuthors();
    }, []);

    const handleDelete = async (bookId) => {
        try {
            console.log(bookId);
            const response = await fetch(`http://localhost:8098/admin/book?bookId=${bookId}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            setSuccess('Xoá sách thành công!');
            fetchBooks();
            setDeleteBookId(null);
        } catch (error) {
            console.error('Error deleting Author:', error);
            setError('Không thể xoá tác giả!');
        }
    };

    React.useEffect(() => {
        let errorTimeout, successTimeout;

        if (error) {
            errorTimeout = setTimeout(() => {
                setError(null);
            }, 3000);
        }

        if (success) {
            successTimeout = setTimeout(() => {
                setSuccess(null);
            }, 3000);
        }

        return () => {
            clearTimeout(errorTimeout);
            clearTimeout(successTimeout);
        };
    }, [error, success]);

    const searchBooks = async () => {
        // Hàm tìm kiếm dựa trên từ khóa
        try {
            const response = await fetch(`http://localhost:8098/admin/book/search?keyword=${searchKeyword}`, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            const formattedBooks = data.map((book) => ({
                ...book,
                publicationYear: dayjs(book.publicationYear).format('YYYY-MM-DD'),
                typeName: book.types[0]?.name || '',
                authorName: book.authors[0]?.name || '',
            }));
            setTotalPages(1);
            setBooks(formattedBooks);
        } catch (error) {
            console.error('Error searching books:', error);
        }
    };

    const filterBooks = async () => {
        try {
            const response = await fetch(
                `http://localhost:8098/admin/book/filter?minPrice=${minPrice}&maxPrice=${maxPrice}&typeName=${typeName}&authorName=${authorName}`,
                {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('token')}`,
                    },
                },
            );
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            const formattedFillterBooks = data.map((book) => ({
                ...book,
                publicationYear: dayjs(book.publicationYear).format('YYYY-MM-DD'),
                typeName: book.types[0]?.name || '',
                authorName: book.authors[0]?.name || '',
            }));

            setBooks(formattedFillterBooks);
            setTotalPages(1);
            console.log(formattedFillterBooks);
        } catch (error) {
            console.error('Error filter books:', error);
        }
    };

    // Xử lý sự kiện onChange của ô search để cập nhật từ khóa tìm kiếm
    const handleSearchChange = (event) => {
        setSearchKeyword(event.target.value);
    };

    useEffect(() => {
        const fetchPointBooks = async () => {
            try {
                const response = await axios.get('http://localhost:8098/admin/book/all?size=30', {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('token')}`,
                    },
                });
                const books = response.data.content[0].content;
                const pointPrices = books.map((book) => book.pointPrice);
                const sortedUniquePointPrices = [...new Set(pointPrices)].sort((a, b) => a - b);

                setMinPrice(sortedUniquePointPrices[0]);
                setMaxPrice(sortedUniquePointPrices[sortedUniquePointPrices.length - 1]);
                setPointBooks(sortedUniquePointPrices);
            } catch (error) {
                console.error('Error fetching types:', error);
            }
        };
        fetchPointBooks();
    }, []);

    // Xử lý sự kiện khi submit form search
    const handleSearchSubmit = (event) => {
        event.preventDefault(); // Ngăn chặn việc reload trang khi submit form
        if (!searchKeyword.trim()) {
            fetchBooks();
            return;
        }
        if (searchKeyword.trim().length > 100) {
            setError('Từ khóa tìm kiếm quá dài, vui lòng nhập ít hơn 100 ký tự');
            return;
        }
        searchBooks();
    };

    const handleAddBookSuccess = () => {
        fetchBooks();
    };

    const handleFilter = () => {
        if (minPrice === '' || maxPrice === '' || typeName === '' || authorName === '') {
            setError('Vui lòng điền đầy đủ thông tin cho tất cả các trường.');
        } else {
            setError('');
            filterBooks();
        }
    };

    return (
        <>
            <div className="d-flex justify-content-between mb-5">
                <form onSubmit={handleSearchSubmit}>
                    <Search>
                        <SearchIconWrapper>
                            <SearchIcon />
                        </SearchIconWrapper>
                        <StyledInputBase
                            placeholder="Tìm tên sách..."
                            inputProps={{ 'aria-label': 'search' }}
                            value={searchKeyword}
                            onChange={handleSearchChange}
                        />
                    </Search>
                </form>
                <div>
                    <Grid container spacing={2} alignItems="flex-end">
                        <Grid item xs={12} sm={6} md={3}>
                            <FormControl fullWidth sx={{ minWidth: 150 }}>
                                <InputLabel id="min-price-label">Giá tối thiểu</InputLabel>
                                <Select
                                    labelId="min-price-label"
                                    id="min-price-select"
                                    value={minPrice}
                                    label="Giá tối thiểu"
                                    onChange={handleMinPriceChange}
                                    MenuProps={{
                                        PaperProps: {
                                            style: {
                                                maxHeight: 300,
                                                width: 250,
                                            },
                                        },
                                    }}
                                >
                                    <MenuItem value="">
                                        <em>None</em>
                                    </MenuItem>
                                    {pointBooks
                                        .filter((price) => price < maxPrice)
                                        .map((price) => (
                                            <MenuItem key={price} value={price}>
                                                {price}
                                            </MenuItem>
                                        ))}
                                </Select>
                            </FormControl>
                        </Grid>

                        <Grid item xs={12} sm={6} md={3}>
                            <FormControl fullWidth sx={{ minWidth: 150 }}>
                                <InputLabel id="max-price-label">Giá tối đa</InputLabel>
                                <Select
                                    labelId="max-price-label"
                                    id="max-price-select"
                                    value={maxPrice}
                                    label="Giá tối đa"
                                    onChange={handleMaxPriceChange}
                                    MenuProps={{
                                        PaperProps: {
                                            style: {
                                                maxHeight: 300,
                                                width: 250,
                                            },
                                        },
                                    }}
                                >
                                    <MenuItem value="">
                                        <em>None</em>
                                    </MenuItem>
                                    {pointBooks
                                        .filter((price) => price > minPrice)
                                        .map((price) => (
                                            <MenuItem key={price} value={price}>
                                                {price}
                                            </MenuItem>
                                        ))}
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={6} md={3}>
                            <FormControl fullWidth sx={{ minWidth: 120 }}>
                                <InputLabel id="type-name-label">Loại sách</InputLabel>
                                <Select
                                    labelId="type-name-label"
                                    id="type-name-select"
                                    value={typeName}
                                    label="Loại sách"
                                    onChange={handleTypeNameChange}
                                    MenuProps={{
                                        PaperProps: {
                                            style: {
                                                maxHeight: 300,
                                                width: 250,
                                            },
                                        },
                                    }}
                                >
                                    <MenuItem value="">
                                        <em>None</em>
                                    </MenuItem>
                                    {typeOptions.map((type, index) => (
                                        <MenuItem key={index} value={type.label}>
                                            {type.label}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={6} md={3}>
                            <FormControl fullWidth sx={{ minWidth: 150 }}>
                                <InputLabel id="author-name-label">Tác giả</InputLabel>
                                <Select
                                    labelId="author-name-label"
                                    id="author-name-select"
                                    value={authorName}
                                    label="Tác giả"
                                    onChange={handleAuthorNameChange}
                                    MenuProps={{
                                        PaperProps: {
                                            style: {
                                                maxHeight: 300,
                                                width: 250,
                                            },
                                        },
                                    }}
                                >
                                    <MenuItem value="">
                                        <em>None</em>
                                    </MenuItem>
                                    {authorOptions.map((author, index) => (
                                        <MenuItem key={index} value={author.label}>
                                            {author.label}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>

                        <Grid item xs={12} sm={6} md={3}>
                            <Button variant="contained" onClick={handleFilter}>
                                Lọc
                            </Button>
                        </Grid>
                    </Grid>
                </div>
                <Stack spacing={2}>{error && <Alert severity="error">{error}</Alert>}</Stack>
                <Stack spacing={2}>{success && <Alert severity="success">{success}</Alert>}</Stack>
                <Button
                    variant="contained"
                    className="mr-3"
                    sx={{
                        bgcolor: '#fcd650',
                        color: 'black',
                        '&:hover': { bgcolor: '#fbbf24' },
                        width: '12%',
                        height: '39px',
                    }}
                    onClick={handleOpen}
                >
                    Thêm sách
                </Button>
            </div>

            <TableContainer component={Paper}>
                <Table sx={{ minWidth: 650 }} aria-label="simple table">
                    <TableHead className="bg-yellow-400">
                        <TableRow>
                            <TableCell align="left" style={{ width: '30%' }}>
                                Tên Sách
                            </TableCell>
                            <TableCell align="left" style={{ width: '10%' }}>
                                Point Price
                            </TableCell>
                            <TableCell align="left" style={{ width: '10%' }}>
                                Năm Xuất Bản
                            </TableCell>
                            <TableCell align="left" style={{ width: '15%' }}>
                                Loại sách
                            </TableCell>
                            <TableCell align="left" style={{ width: '15%' }}>
                                Tác giả
                            </TableCell>
                            <TableCell align="left" style={{ width: '20%' }}>
                                Action
                            </TableCell>
                        </TableRow>
                    </TableHead>

                    <TableBody>
                        {books.map((book) => (
                            <TableRow key={book.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                <TableCell component="th" scope="row" sx={{ wordBreak: 'break-word' }}>
                                    {book.name}
                                </TableCell>
                                <TableCell align="left">{book.pointPrice}</TableCell>
                                <TableCell align="left">{book.publicationYear}</TableCell>
                                <TableCell align="left" sx={{ wordBreak: 'break-word' }}>
                                    {book.typeName}
                                </TableCell>
                                <TableCell align="left">{book.authorName}</TableCell>
                                <TableCell align="center">
                                    <div style={{ display: 'flex', justifyContent: 'space-between', width: '70%' }}>
                                        <Button
                                            variant="outlined"
                                            color="primary"
                                            onClick={() => handleUpdate(book.id)}
                                        >
                                            Cập nhật
                                        </Button>
                                        <Button
                                            variant="outlined"
                                            color="error"
                                            onClick={() => setDeleteBookId(book.id)}
                                        >
                                            Xoá
                                        </Button>
                                    </div>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <Stack
                spacing={2}
                sx={{ marginTop: '30px', textAlign: 'center', justifyContent: 'center', alignItems: 'center' }}
            >
                <Pagination
                    sx={{
                        '& .MuiPaginationItem-root.Mui-selected': {
                            color: '#000',
                            bgcolor: '#FFD700',
                        },
                        '& .MuiButtonBase-root': {
                            '&:hover': {
                                bgcolor: '#FFECB3',
                            },
                        },
                    }}
                    count={totalPages}
                    color="secondary"
                    page={currentPage}
                    onChange={handlePageChange}
                />
            </Stack>
            <Modal
                open={open}
                onClose={handleClose}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"
            >
                <Box sx={style}>
                    <AddNewBook
                        handleClose={handleClose}
                        handleAddBookSuccess={handleAddBookSuccess}
                        bookId={addBook}
                    />
                </Box>
            </Modal>

            <Modal
                open={showUpdateModal}
                onClose={handleCloseUpdateModal}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"
            >
                <Box sx={style}>
                    <UpdateBook
                        handleClose={handleCloseUpdateModal}
                        handleAddBookSuccess={handleAddBookSuccess}
                        bookId={updatingBook}
                    />
                </Box>
            </Modal>

            <Dialog
                open={deleteBookId !== null}
                onClose={() => setDeleteBookId(null)}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">Xác nhận xoá</DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        Bạn chắc chắn muốn xoá sách này?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDeleteBookId(null)} color="primary">
                        Huỷ
                    </Button>
                    <Button onClick={() => handleDelete(deleteBookId)} color="primary" autoFocus>
                        Xoá
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
}
